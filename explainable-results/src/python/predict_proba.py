import sys
from lime.lime_text import LimeTextExplainer
import re
import numpy as np
# load a couple of models, run lime on both for the top instances, and then see what the explanations are for those
# instances how to define "top instances" : 1) make a ranking of which results contribute most to the MAP, NDCG@k,
# etc. what I need for this: qrels, rankings of both models, then compute metrics, then go one by one through
# results, find the change in metric after removing a result, then get instances, run lime on them.


class Pred:
    def __init__(self):
        self.corpus = None
        self.query = ""
        self.doc = ""
        self.topscore = -(sys.maxsize - 5)
        self.kthscore = -(sys.maxsize - 5)
        self.kthrank = -1

    def _get_prob(self, score=0.0, rank=0, type="score", k=1):

        if type == "score":
            return 1 - ((self.topscore - score) / self.topscore)
        elif type == "topk":
            return 1 if self.kthscore <= score else 0.0
        elif type == "rank":
            return 1 - (1.0 * rank) / k if self.kthrank >= rank else 0.0

    def read_mapped_file(self):
        a = dict()
        tokenizer = lambda doc: re.compile(r"(?u)\b\w\w+\b").findall(doc)
        with open("relation.txt", "r+") as f:
            for line in f.readlines():
                splitline = line.strip("\n").split(" ")
                if splitline[1] not in a:
                    a[splitline[1]] = list()
                a[splitline[1]].append(splitline[2])
        
        corpus = dict()
        with open("corpus.txt", "r+") as f:
            for line in f.readlines():
                splitline = line.strip("\n").split(" ")
                corpus[splitline[0]] = " ".join(splitline[1:])

        for query in a:
            for doc in a[query]:
                self.query = query
                self.doc = doc
                document_text = corpus[doc]
                explainer = LimeTextExplainer(class_names=["irrelevant", "relevant"], split_expression=tokenizer)
                exp = explainer.explain_instance(document_text, self.predict_proba, 10)
                print(exp.as_list())

        return

    def predict_proba(self, doc_text):
        self.set_refs()

        with open("predict.test.drmm.wikiqa.txt", "r+") as f:
            lines = f.readlines()
            for line in lines:
                splitline = line.strip("\n").split(" ")
                if splitline[0] == self.query and splitline[2] == self.doc:
                    p =  self._get_prob(rank=int(splitline[3]), score=float(splitline[4]))
                    return np.array([[p,1-p],[1-p,p]])
        return np.array([[1,0],[0,1]])

    def set_refs(self):
        count = 0
        self.kthrank = 5
        with open("predict.test.drmm.wikiqa.txt", "r+") as f:
            lines = f.readlines()
            for line in lines:
                splitline = line.split(" ")
                if splitline[0] == self.query:
                    count+=1
                    if count == 1:
                        self.topscore = float(splitline[4])
                    if count == self.kthrank:
                        self.kthscore = float(splitline[4])
                        break
        return


def main():
    p = Pred()
    p.read_mapped_file()
    return


if __name__ == '__main__':
    main()
