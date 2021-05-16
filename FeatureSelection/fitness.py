from LinReg import LinReg
import numpy as np


x = None
y = None
lr = LinReg()
bitstring_dict = {}

"""Process dataset and extract y values"""
with open('FeatureSelection/resources/dataset.txt', 'r') as f:
    lines = f.readlines()
    x = [line.split(',') for line in lines]
x = np.asarray(x, dtype=np.float64)
y = x[:, [101]]
x = np.delete(x, 101, 1)


def get_fitness_no_feat_select():
    x_no_feat_select = lr.get_columns(x, ['1'] * 101)
    return lr.get_fitness(x_no_feat_select, y)


def get_fitness(bitstring):
    if bitstring in bitstring_dict:
        return bitstring_dict[bitstring]
    fitness = -lr.get_fitness(lr.get_columns(x, bitstring), y)
    bitstring_dict[bitstring] = fitness
    return fitness
