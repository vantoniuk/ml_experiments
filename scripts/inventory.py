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

print("reading first data frame...")
df1 = pd.read_csv('/Users/vitalii.antoniuk/projects/ml_experiments/temp/retailrocket-recommender-system-dataset/item_properties_part1.csv')

print("reading second data frame...")
df2 = pd.read_csv('/Users/vitalii.antoniuk/projects/ml_experiments/temp/retailrocket-recommender-system-dataset/item_properties_part2.csv')

print("appending and filling na...")
df = df1.append(df2, ignore_index=True).fillna(value="")

print("Group by and aggregation...")
z = df.groupby(i).aggregate({t: pd.Series.tolist,
                             i: 'first',
                             p: pd.Series.tolist,
                             v: pd.Series.tolist
                             })

print("Transformation...")
x = z.transform({t: "|".join,
                 i: lambda x: x,
                 p: "|".join,
                 v: "|".join
                 })

print("Saving to csv...")
x.to_csv("aggregated.csv")