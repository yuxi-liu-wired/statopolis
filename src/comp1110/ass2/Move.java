package comp1110.ass2;

/**
 * A tuple representing a move.
 * It is a more specific class than Piece. Think of it as a specialized version of Piece for this specific version of
 * Stratopolis that we are programming.
 *
 * Created by Yuxi Liu (u5950011) on 8/13/16.
 */
public class Move {
    public Coordinate origin;
    public String pieceName;
    public String orientation;

    public Move(Coordinate origin, String pieceName,String orientation) {
        this.origin = origin;
        this.pieceName = pieceName;
        this.orientation = orientation;
    }

    /**
     * Returns a Piece object that this Move corresponds to.
     * @return the Piece object that this Move corresponds to.
     */
    public Piece toPiece() {
        Piece p = new Piece(pieceName);
        p.translateTo(origin);
        switch (orientation) {
            case "A":
                break;
            case "B":
                p.rotate90CW();
                break;
            case "C":
                p.rotate180CW();
                break;
            case "D":
                p.rotate270CW();
                break;
        }
        return p;
    }

    @Override
    public String toString() {
        return (origin + pieceName + orientation);
    }
}
