class Data:
    def __init__(self, data : list):
        self.id = data[0]
        self.name : str = data[1]
        self.password :str= data[2]
        self.forced : bool = data[3]

    def __eq__(self, other):
        return self.name == other

    def __repr__(self):
        return (self.name,) + " " + (self.forced,)

