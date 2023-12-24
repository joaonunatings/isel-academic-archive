import { GridCell } from "./grid-cell";
import { Shift } from "../shift";

export class ShiftGridCell implements GridCell {
    id: string;
    text: string;
    color: string;

    shift: Shift;

    constructor(id: string, text: string, color: string, shift: Shift) {
        this.id = id
        this.text = text
        this.color = color
        this.shift = shift
    }
}
