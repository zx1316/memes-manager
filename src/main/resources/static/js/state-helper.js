let stateHelper = {
    stateMap: new Map(),
    elementMap: new Map(),
    createState(state, init) {
        this.stateMap.set(state, {elements: new Set(), value: init});
    },
    deleteState(state) {
        this.stateMap.get(state).elements.forEach(ele => {
            this.elementMap.get(ele).states.forEach(state1 => this.stateMap.get(state1).elements.delete(ele));
            this.elementMap.delete(ele);
        });
        this.stateMap.delete(state);
    },
    addObserver(element, callback, ...states) {
        states.forEach(state => this.stateMap.get(state).elements.add(element));
        this.elementMap.set(element, {states: states, callback: callback});
        const arr1 = [];
        states.forEach(state1 => arr1.push(this.stateMap.get(state1).value));
        callback(element, ...arr1);
    },
    removeObserver(element) {
        this.elementMap.get(element).states.forEach(state => this.stateMap.get(state).elements.delete(element));
        this.elementMap.delete(element);
    },
    setState(state, value) {
        const stateObj = this.stateMap.get(state);
        stateObj.value = value;
        stateObj.elements.forEach(ele => {
            const eleObj = this.elementMap.get(ele);
            const arr1 = [];
            eleObj.states.forEach(state1 => arr1.push(this.stateMap.get(state1).value));
            eleObj.callback(ele, ...arr1);
        });
    },
    getState(state) {
        return this.stateMap.get(state).value;
    },
    getObservers(state) {
        return Array.from(this.stateMap.get(state).elements);
    }
};
