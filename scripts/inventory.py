import pandas as pd

"""
Inventory create from kaggle reateil rocket data set

https://www.kaggle.com/retailrocket/ecommerce-dataset
"""

# Columns
t = 'timestamp'
i = 'itemid'
p = 'property'
v = 'value'

df1 = pd.read_csv('/Users/vitalii.antoniuk/projects/ml_experiments/temp/retailrocket-recommender-system-dataset/item_properties_part1.csv')
df2 = pd.read_csv('/Users/vitalii.antoniuk/projects/ml_experiments/temp/retailrocket-recommender-system-dataset/item_properties_part2.csv')

df = df1.append(df2, ignore_index=True)

z = df.groupby(i).aggregate({t: 'first',
                             i: 'first',
                             p: pd.Series.tolist,
                             v: pd.Series.tolist
                             })

x = z.transform({t: lambda x: x,
                 i: lambda x: x,
                 p: "|".join,
                 v: "|".join
                 })


x.to_csv("aggregated.csv")