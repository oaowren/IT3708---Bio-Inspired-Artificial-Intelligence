import matplotlib.pyplot as plt
import matplotlib.colors as mcolors


def remove_low_contrast(color):
    r = color[1:3]
    g = color[3:5]
    b = color[5:7]
    for i in [r, g, b]:
        if i > "E5":
            return False
    return True

colors = [c for c in list(mcolors.CSS4_COLORS.values()) if remove_low_contrast(c)]

def visualize_routes(filename):
    customer_list = []
    depot_list = []
    vehicles = []
    count = 1
    with open("project2/Data Files/"+filename) as f:
        depot_info = f.readline().split()
        for i in range(int(depot_info[2])):
            next(f)
        for line in f:
            new_arr = line.split()
            if len(new_arr) == 7:
                depot_list.append({"id": count, "x": int(new_arr[1]), "y": int(new_arr[2])})
                count += 1
            else:
                customer_list.append({"id": int(new_arr[0]), "x": int(new_arr[1]), "y": int(new_arr[2])})
    with open("project2/Results/"+filename) as f:
        next(f)
        for line in f:
            new_arr = line.split()
            vehicles.append({"depot": int(new_arr[0]), "id": int(new_arr[1]), "route": new_arr[5:-1]})
    plt.plot()
    for c in customer_list:
        plt.plot(c["x"], c["y"], 'ro', markersize=2, label="C"+str(c["id"]))
    for d in depot_list:
        plt.plot(d["x"], d["y"], 'bo', markersize=5, label="D"+str(d["id"]))
    for i in range(len(vehicles)):
        plot_route(vehicles[i], depot_list, customer_list, colors[i])
    plt.axis('off')
    plt.show()


def get_customer_pos(id, customers):
    for c in customers:
        if c["id"] == int(id):
            return c


def get_depot_pos(id, depots):
    for d in depots:
        if d["id"] == id:
            return d
        

def plot_route(v, depots, customers, color):
    line_thickness = 1
    route = v["route"]
    depot = v["depot"]
    depot_pos = get_depot_pos(depot, depots)
    first_customer = get_customer_pos(route[0], customers)
    plt.plot((first_customer["x"], depot_pos["x"]), (first_customer["y"], depot_pos["y"]), linewidth=line_thickness, c=color)
    for c in route[1:]:
        next_customer = get_customer_pos(c, customers)
        plt.plot((first_customer["x"], next_customer["x"]), (first_customer["y"], next_customer["y"]), linewidth=line_thickness, c=color)
        first_customer = next_customer
    plt.plot((depot_pos["x"], next_customer["x"]), (depot_pos["y"], next_customer["y"]), linewidth=line_thickness, c=color)


def visualize_generations(filename):
    generations = []
    fitness = []
    with open("project2/Generations/"+filename) as f:
        for line in f:
            arr = line.split()
            generations.append(int(arr[0]))
            fitness.append(float(arr[1].replace(",",".")))
    plt.title("Fittest individual per generation, "+filename)
    plt.plot(generations, fitness)
    plt.xlabel("Generation")
    plt.ylabel("Total route distance")
    plt.show()


if __name__ == '__main__':
    problem = "p01"
    visualize_generations(problem)
    visualize_routes(problem)