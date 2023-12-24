package pt.isel.tsma.exception.model.shift;

import pt.isel.tsma.entity.model.shift.Shift;
import pt.isel.tsma.exception.InvalidParameterException;

import static pt.isel.tsma.exception.model.Code.INVALID_SHIFT;

public class InvalidShiftException extends InvalidParameterException {
	public InvalidShiftException(Shift.ShiftId shiftId) {
		super(INVALID_SHIFT, "Shift #" + shiftId + " not found or with invalid ID");
	}
}
