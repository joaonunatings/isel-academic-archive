Date.prototype.addHours = function(h) {
    this.setTime(this.getTime() + (h*60*60*1000));
    return this;
}

Date.prototype.addSeconds = function(s) {
    this.setTime(this.getTime() + (s*1000));
    return this;
}

export function randomValueString(size) {
    const array = crypto.getRandomValues(new Uint8Array(size))
    return array.join('')
}

export function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}