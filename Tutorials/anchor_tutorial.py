
# coding: utf-8

# In[1]:

from __future__ import print_function

# get_ipython().run_line_magic('load_ext', 'autoreload')
# get_ipython().run_line_magic('autoreload', '2')
import os
import os.path
import numpy as np
import sklearn
import sklearn.model_selection
import sklearn.linear_model
import sklearn.ensemble
import spacy
import sys
from sklearn.feature_extraction.text import CountVectorizer
from anchor import anchor_text


# In[2]:


# dataset from http://www.cs.cornell.edu/people/pabo/movie-review-data/
def load_polarity(path='/Users/dhruva/Desktop/ISR/final_project/MatchZoo/rt-polaritydata/'):
    data = []
    labels = []
    f_names = ['rt-polarity.neg', 'rt-polarity.pos']
    for (l, f) in enumerate(f_names):
        for line in open(os.path.join(path, f), 'rb'):
            try:
                line.decode('utf8')
            except:
                continue
            data.append(line.strip())
            labels.append(l)
    return data, labels


# Note: you must have spacy installed. Run:
# 
#         pip install spacy && python -m spacy download en_core_web_lg

# In[3]:


nlp = spacy.load('en_core_web_lg')


# In[4]:


data, labels = load_polarity()
train, test, train_labels, test_labels = sklearn.model_selection.train_test_split(data, labels, test_size=.2, random_state=42)
train, val, train_labels, val_labels = sklearn.model_selection.train_test_split(train, train_labels, test_size=.1, random_state=42)
train_labels = np.array(train_labels)
test_labels = np.array(test_labels)
val_labels = np.array(val_labels)


# In[5]:


vectorizer = CountVectorizer(min_df=1)
vectorizer.fit(train)
train_vectors = vectorizer.transform(train)
test_vectors = vectorizer.transform(test)
val_vectors = vectorizer.transform(val)


# In[6]:


c = sklearn.linear_model.LogisticRegression()
# c = sklearn.ensemble.RandomForestClassifier(n_estimators=500, n_jobs=10)
c.fit(train_vectors, train_labels)
preds = c.predict(val_vectors)
print('Val accuracy', sklearn.metrics.accuracy_score(val_labels, preds))
def predict_lr(texts):
    return c.predict(vectorizer.transform(texts))


# ### Explaining a prediction
# use_unk_distribution=True means we will perturb examples by replacing words with UNKS

# In[7]:


explainer = anchor_text.AnchorText(nlp, ['negative', 'positive'], use_unk_distribution=True)


# In[8]:


np.random.seed(1)
text = 'This is a good book .'
pred = explainer.class_names[predict_lr([text])[0]]
alternative =  explainer.class_names[1 - predict_lr([text])[0]]
print('Prediction: %s' % pred)
exp = explainer.explain_instance(text, predict_lr, threshold=0.95, use_proba=True)


# Let's take a look at the anchor. Note that using this perturbation distribution, having the word 'good' in the text virtually guarantees a positive prediction

# In[9]:


print('Anchor: %s' % (' AND '.join(exp.names())))
print('Precision: %.2f' % exp.precision())
print()
print('Examples where anchor applies and model predicts %s:' % pred)
print()
print('\n'.join([x[0] for x in exp.examples(only_same_prediction=True)]))
print()
print('Examples where anchor applies and model predicts %s:' % alternative)
print()
print('\n'.join([x[0] for x in exp.examples(partial_index=0, only_different_prediction=True)]))


# ### Changing the distribution
# Let's try this with another perturbation distribution, namely one that replaces words by similar words instead of UNKS

# In[10]:


explainer = anchor_text.AnchorText(nlp, ['negative', 'positive'], use_unk_distribution=False)


# In[11]:


np.random.seed(1)
text = 'This is a good book .'
pred = explainer.class_names[predict_lr([text])[0]]
alternative =  explainer.class_names[1 - predict_lr([text])[0]]
print('Prediction: %s' % pred)
exp = explainer.explain_instance(text, predict_lr, threshold=0.95, use_proba=True)


# Let's take a look at the anchor now. Note that with this distribution, we need more to guarantee a prediction of positive.

# In[12]:


print('Anchor: %s' % (' AND '.join(exp.names())))
print('Precision: %.2f' % exp.precision())
print()
print('Examples where anchor applies and model predicts %s:' % pred)
print()
print('\n'.join([x[0] for x in exp.examples(only_same_prediction=True)]))
print()
print('Examples where anchor applies and model predicts %s:' % alternative)
print()
print('\n'.join([x[0] for x in exp.examples(only_different_prediction=True)]))


# Let's take a look at the partial anchor 'good' to see why it's not sufficient in this case
# 

# In[13]:


print('Partial anchor: %s' % (' AND '.join(exp.names(0))))
print('Precision: %.2f' % exp.precision(0))
print()
print('Examples where anchor applies and model predicts %s:' % pred)
print()
print('\n'.join([x[0] for x in exp.examples(partial_index=0, only_same_prediction=True)]))
print()
print('Examples where anchor applies and model predicts %s:' % alternative)
print()
print('\n'.join([x[0] for x in exp.examples(partial_index=0, only_different_prediction=True)]))


# ## See a visualization of the anchor with examples and etc (won't work if you're seeing this on github)

# In[ ]:


exp.show_in_notebook()

