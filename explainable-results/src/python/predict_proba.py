import sys
import lime
from lime.lime_text import LimeTextExplainer


# load a couple of models, run lime on both for the top instances, and then see what the explanations are for those instances
# how to define "top instances" : 1) make a ranking of which results contribute most to the MAP, NDCG@k, etc.
# what I need for this: qrels, rankings of both models, then compute metrics, then go one by one through results, find the change in metric after removing a result, then get instances, run lime on them.


class Pred:
    def __init__(self):
        self.corpus = None
        self.query = ""
        self.topscore = -(sys.maxsize - 5)
        self.kthscore = -(sys.maxsize - 5)
        self.kthrank = -1

    def _get_prob(self, score=0.0, rank=0, type="score", k=1):

        if type == "score":
            return 1 - ((self.topscore - score) / self.topscore)
        elif type == "topk":
            return 1 if self.kthscore <= score else 0
        elif type == "rank":
            return 1 - (1.0 * rank) / k if self.kthrank >= rank else 0

    def read_file(self, filename):
        k = 10
        namelist = []
        textlist = []
        scorelist = []
        with open(filename, "r+") as f:
            for line in f.readlines():
                splitline = line.split(" ")
                namelist.append(splitline[0])
                textlist.append(splitline[1])
                scorelist.append(splitline[2])

        self.topscore = scorelist[0]
        self.kthscore = scorelist[k]
        self.corpus = list(zip([namelist, textlist, scorelist]))
        self.kthrank = k
        return

    def read_mapped_file(self):
        a = dict()
        with open("relation.txt", "r+") as f:
            for line in f.readlines():
                splitline = line.split(" ")
                if splitline[1] not in a:
                    a[splitline[1]] = list()
                a[splitline[1]].append(splitline[2])
        
        corpus = dict()
        with open("corpus.txt", "r+") as f:
            for line in f.readlines():
                splitline = line.split(" ")
                corpus[splitline[0]] = " ".join(splitline[1:])

        for query in a:
            for doc in a[query]:
                self.query = query
                document_text = corpus[doc]
                explainer = LimeTextExplainer(class_names=["relevant", "irrelevant"])
                explainer.explain_instance(document_text, self.predict_proba, 10)



        return

    def predict_proba(self, doc_text):
        doc = self.find_docno(doc_text)
        with open("predict.test.drmm.Testing.txt", "r+") as f:
            for line in f.readlines():
                splitline = line.split(" ")
                if splitline[0] == query and splitline[2] == doc:
                    return self._get_prob(rank=splitline[3],score=splitline[4])
        return 0

    def lime_func(self):
        return


def main():
    filename = "/Users/dhruva/Desktop/ISR/final_project/explainable-results/file_text.txt"
    p = Pred()
    p.read_file(filename)

    p.predict_proba()
    return


if __name__ == '__main__':
    main()
