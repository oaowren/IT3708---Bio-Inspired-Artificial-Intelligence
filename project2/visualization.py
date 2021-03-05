import matplotlib.pyplot as plt
import numpy as np


def visualize_routes(filename):
    customer_list = []
    depot_list = []
    vehicles = []
    with open("project2/Data Files/"+filename) as f:
        depot_info = f.readline().split()
        for i in range(int(depot_info[2])):
            next(f)
        for line in f:
            new_arr = line.split()
            if len(new_arr) == 7:
                depot_list.append({"id": int(new_arr[0]), "x": int(new_arr[1]), "y": int(new_arr[2])})
            else:
                customer_list.append({"id": int(new_arr[0]), "x": int(new_arr[1]), "y": int(new_arr[2])})
    with open("project2/Results/"+filename) as f:
        next(f)
        for line in f:
            new_arr = line.split()
            print(new_arr[5:-1])
            vehicles.append({"depot": int(new_arr[0]), "id": int(new_arr[1]), "route": new_arr[5:-1]})
    plt.plot() 
    for c in customer_list:
        plt.plot(c["x"], c["y"], 'ro', markersize=1, label="C"+str(c["id"]))
    for d in depot_list:
        plt.plot(d["x"], d["y"], 'bo', markersize=3, label="D"+str(d["id"]))
    plt.axis('off')
    plt.show()
        
if __name__ == '__main__':
    visualize_routes("p01")