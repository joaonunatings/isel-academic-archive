import { GridCell } from "./grid-cell";
import { Member } from "../member";

export class MemberGridCell implements GridCell {
    id: string;
    text: string;
    color: string;

    member: Member;

    constructor(id: string, text: string, color: string, member: Member) {
        this.id = id
        this.text = text
        this.color = color
        this.member = member
    }
}
