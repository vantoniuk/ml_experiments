# Word2Vec Demo

Simple scala project showcase of Word2Vec capabilities with web interface provided.

Parameters used for training chosen based on:

* Training time on local machine, hence some parameter values can be incresed for better accuracy
* Inspired by these 2 papers [Word2Vec Explained](https://arxiv.org/pdf/1402.3722v1.pdf) and original Google paper [Distributed Representations of Words and Phrases and their Compositionality](https://arxiv.org/pdf/1310.4546.pdf)
* Inferred reading deeplearning4j source code

```
  new Word2Vec.Builder()
    .minWordFrequency(25)
    .iterations(2)
    .epochs(1)
    .layerSize(200)
    .batchSize(25)
    .seed(42)
    .useVariableWindow(5, 7)
    .negativeSample(0.1)
    .sampling(0.01)
```

### Train the model

To train the model you need to run wordembedding.scripts.Word2VecEmbedding script.
Make sure you satisfied with chosen parameters. The script will read file provided as **source** argument
and will save the model in a file, provided as **output** argument.
E.g.: You can run it using sbt with mem option specified:

```
sbt -mem 12000 "project word2vec" \
"run-main wordembedding.scripts.Word2VecEmbedding \
 --source TEXT_SOURCE_LOCAL_PATH --output MODEL_LOCAL_PATH"
```

if you need to pick column from csv file, you can specify csv-column option
E.g.: to pick only text for column 2

```
sbt -mem 12000 "project word2vec" \
"run-main wordembedding.scripts.Word2VecEmbedding \
 --source TEXT_SOURCE_LOCAL_PATH --output MODEL_LOCAL_PATH
 --preprocess --csv-column 2"
```

for testging purposes you can use first N lines of original file
E.g.: to concider only first 10000 lines

```
sbt -mem 12000 "project word2vec" \
"run-main wordembedding.scripts.Word2VecEmbedding \
 --source TEXT_SOURCE_LOCAL_PATH --output MODEL_LOCAL_PATH
 --preprocess --csv-column 1
 --lines 10000
```

### Run web interface

**Make sure to specify model file**

```
word2vec {
  model_file = MODEL_LOCAL_PATH
}
```

Then you can run play project

```
sbt "project word2vec_web" run
```

Access the web page busing regular [play homepage url](http://localhost:9000)

Have fun...
