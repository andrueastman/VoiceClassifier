import numpy

def calculate_mean(list):
    total = 0;
    for index in range(len(list)):
        total += list[index]

    return total / len(list)

def first_quartile(list):
    if (len(list)%2) == 0:
        elems = int(len(list) / 2)
    else:
        elems = int((len(list) / 2) + 1)

    return median(sorted(list[0:elems]))

def third_quartile(list):
    if (len(list)%2) == 0:
        elems =int(len(list)/2)
    else:
        elems = int((len(list)/2) +1)

    return median(sorted(list[elems:len(list)]))

def inter_quartile_range(list):
    return third_quartile(list)-first_quartile(list)

def std_dev(list):
    return numpy.std(numpy.array(list))

def median(list):
    quotient, remainder = divmod(len(list), 2)
    if remainder:
        return sorted(list)[quotient]
    return sum(sorted(list)[quotient - 1:quotient + 1]) / 2.
