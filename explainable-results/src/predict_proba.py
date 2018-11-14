class Pred:
    def __init__(self):
        self.corpus = None
        self.topscore = -(sys.maxint - 5) 
        self.kthscore = -(sys.maxint - 5)
        self.kthrank = -1

    def _make_tfidfvecs(self):
        # vectorizer = TfidfVectorizer()
        # X_sparse = vectorizer.fit_transform(self.corpus)
        # X_mat = X_sparse.toarray()

         
    def _get_prob(self, score, rank=0, type="score"):
        if type == "score":
            return 1 - ((self.topscore - score) / self.topscore)
        elif type == "topk":
            return 1 if self.kthscore <= score else 0 
        elif type == "rank":
            return 1 - (1.0*rank) / k if self.kthrank >= rank else 0 

    
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

    def predict_proba(instance):
        return

    def lime_func():
        return

def main():
    filename = "/Users/dhruva/Desktop/ISR/final_project/explainable-results/file_text.txt"
    p = Pred()
    p.read_file(filename)
    p.predict_proba(l[3])
    return


if __name__ == '__main__':
    main()
