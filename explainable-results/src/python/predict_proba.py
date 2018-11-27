import sys
import lime


# load a couple of models, run lime on both for the top instances, and then see what the explanations are for those instances
# how to define "top instances" : 1) make a ranking of which results contribute most to the MAP, NDCG@k, etc.
# what I need for this: qrels, rankings of both models, then compute metrics, then go one by one through results, find the change in metric after removing a result, then get instances, run lime on them.


class Pred:
    def __init__(self):
        self.corpus = None
        self.topscore = -(sys.maxsize - 5)
        self.kthscore = -(sys.maxsize - 5)
        self.kthrank = -1

    def _make_tfidfvecs(self):
        # vectorizer = TfidfVectorizer()
        # X_sparse = vectorizer.fit_transform(self.corpus)
        # X_mat = X_sparse.toarray()
        pass

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

    def predict_proba(self):
        return

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
