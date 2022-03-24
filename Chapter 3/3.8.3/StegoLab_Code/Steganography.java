import java.awt.Color;
import java.util.ArrayList;
import java.awt.Point;

public class Steganography {

    public static void clearLow(Pixel p) {
        p.setRed((p.getRed() / 4) * 4);
        p.setGreen((p.getGreen() / 4) * 4);
        p.setBlue((p.getBlue() / 4) * 4);
    }

    /**
     * Clear the lower (rightmost) two bits in a pixel.
     */
    public static Picture testClearLow(Picture p) {
        Picture newPic = new Picture(p);
        Pixel[][] pixels = newPic.getPixels2D();
        for (Pixel[] row : pixels) {
            for (Pixel col : row) {
                clearLow(col);
            }
        }
        return newPic;
    }

    /**
     * Set the lower 2 bits in a pixel to the highest 2 bits in c
     */
    public static void setLow(Pixel p, Color c) {
        clearLow(p);

        int red = c.getRed() / 64;
        int green = c.getGreen() / 64;
        int blue = c.getBlue() / 64;

        p.setRed(p.getRed() + red);
        p.setGreen(p.getGreen() + green);
        p.setBlue(p.getBlue() + blue);
    }

    public static Picture testSetLow(Picture p, Color c) {
        Picture newPic = new Picture(p);
        Pixel[][] pixels = newPic.getPixels2D();
        for (Pixel[] row : pixels) {
            for (Pixel col : row) {
                setLow(col, c);
            }
        }
        return newPic;
    }

    /**
     * Sets the highest two bits of each pixel's colors to the lowest two bits of each pixel's
     * colors
     */
    public static Picture revealPicture(Picture hidden) {
        Picture copy = new Picture(hidden);
        Pixel[][] pixels = copy.getPixels2D();
        Pixel[][] source = hidden.getPixels2D();
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Color col = source[r][c].getColor();
                Pixel p = pixels[r][c];
                p.setRed(col.getRed() % 4 * 64);
                p.setGreen(col.getGreen() % 4 * 64);
                p.setBlue(col.getBlue() % 4 * 64);
            }
        }
        return copy;
    }

    /**
     * Determines whether secret can be hidden in source, which is true if source and secret are the
     * same dimensions.
     * 
     * @param source is not null
     * @param secret is not null
     * @return true if secret can be hidden in source, false otherwise.
     */
    public static Boolean canHide(Picture source, Picture secret) {
        int sourceHeight = source.getHeight();
        int sourceWidth = source.getWidth();
        int secretHeight = secret.getHeight();
        int secretWidth = secret.getWidth();

        return (sourceHeight >= secretHeight && sourceWidth >= secretWidth);
    }

    /**
     * Creates a new Picture with data from secret hidden in data from source * @param source is not
     * null
     * 
     * @param secret is not null
     * @return combined Picture with secret hidden in source precondition: source is same width and
     *         height as secret
     */
    public static Picture hidePicture(Picture source, Picture secret) {
        Picture sourcePic = new Picture(source);
        Picture secretPic = new Picture(secret);

        Pixel[][] sourcePixels = sourcePic.getPixels2D();
        Pixel[][] secretPixels = secretPic.getPixels2D();

        Boolean hide = canHide(source, secret);

        if (hide) {
            for (int r = 0; r < secretPixels.length; r++) {
                for (int c = 0; c < secretPixels[0].length; c++) {
                    setLow(sourcePixels[r][c], secretPixels[r][c].getColor());
                }
            }
        } else {
            System.out.println("The source image is smaller than the secret image!");
        }

        return sourcePic;
    }

    public static Picture hidePicture(Picture source, Picture secret, int startRow,
            int startColumn) {
        Picture sourcePic = new Picture(source);
        Picture secretPic = new Picture(secret);

        Pixel[][] sourcePixels = sourcePic.getPixels2D();
        Pixel[][] secretPixels = secretPic.getPixels2D();

        Boolean hide = canHide(source, secret);

        // Use two variables
        // First variable is used for positioning
        // Second variable is used for looping through the secret image and getting the colors
        // We get the colors from the secret image and put them on the source image
        if (hide) {
            for (int r = startRow, r2 = 0; r < sourcePixels.length
                    && r2 < secretPixels.length; r++, r2++) {
                for (int c = startColumn, c2 = 0; c < sourcePixels[0].length - 1
                        && c2 < secretPixels[0].length; c++, c2++) {
                    setLow(sourcePixels[r][c], secretPixels[r2][c2].getColor());
                }
            }
        }

        return sourcePic;
    }

    public static Boolean isSame(Picture p1, Picture p2) {
        Picture p1Pic = new Picture(p1);
        Picture p2Pic = new Picture(p2);

        Pixel[][] p1Pixels = p1Pic.getPixels2D();
        Pixel[][] p2Pixels = p2Pic.getPixels2D();

        if (!(p1Pixels.length == p2Pixels.length) || !(p1Pixels[0].length == p2Pixels[0].length)) {
            return false;
        } else {
            for (int r = 0; r < p1Pixels.length; r++) {
                for (int c = 0; c < p1Pixels[0].length; c++) {
                    Color p1CurColor = p1Pixels[r][c].getColor();
                    Color p2CurColor = p2Pixels[r][c].getColor();
                    if (!(p1CurColor.equals(p2CurColor))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static ArrayList<Point> findDifferences(Picture p1, Picture p2) {
        ArrayList<Point> pointList = new ArrayList<Point>();

        Picture p1Pic = new Picture(p1);
        Picture p2Pic = new Picture(p2);

        Pixel[][] p1Pixels = p1Pic.getPixels2D();
        Pixel[][] p2Pixels = p2Pic.getPixels2D();

        if (!(p1Pixels.length == p2Pixels.length) || !(p1Pixels[0].length == p2Pixels[0].length)) {
            return pointList;
        } else {
            for (int r = 0; r < p1Pixels.length; r++) {
                for (int c = 0; c < p1Pixels[0].length; c++) {
                    Color p1CurColor = p1Pixels[r][c].getColor();
                    Color p2CurColor = p2Pixels[r][c].getColor();
                    if (!(p1CurColor.equals(p2CurColor))) {
                        Point curPoint = new Point(r, c);
                        pointList.add(curPoint);
                    }
                }
            }
        }

        return pointList;
    }

    // showDifferentArea tasks:
    // Get first row
    // Get first column
    // Get last row
    // Get last column

    public static void main(String[] args) {
        Picture beach = new Picture("beach.jpg");
        Picture motorcycle = new Picture("blueMotorcycle.jpg");

        // // // clearLow Sample Code // // //
        // beach.explore();
        // Picture copy = testClearLow(beach);
        // copy.explore();
        // // // // // //

        // // // setLow Sample Code // // //
        // Picture beach2 = new Picture("beach.jpg");
        // beach2.explore();
        // Picture copy2 = testSetLow(beach2, Color.PINK);
        // copy2.explore();
        // Picture copy3 = revealPicture(copy2);
        // copy3.explore();
        // // // // // //

        // // // canHide Sample Code // // //
        // beach.explore();
        // if (canHide(beach, motorcycle) == true) {
        // Picture copy = hidePicture(beach, motorcycle);
        // copy.explore();
        // Picture copy2 = revealPicture(copy);
        // copy2.explore();
        // }
        // // // // // //

        // // // hidePicture2 Sample Code // // //
        // Picture robot = new Picture("robot.jpg");
        // Picture flower1 = new Picture("flower1.jpg");
        // beach.explore();

        // // these lines hide 2 pictures
        // Picture hidden1 = hidePicture(beach, robot, 65, 208);
        // Picture hidden2 = hidePicture(hidden1, flower1, 280, 110);

        // hidden2.explore();
        // Picture unhidden = revealPicture(hidden2);
        // unhidden.explore();
        // // // // // //

        // // // isSame Sample Code // // //
        // Picture swan = new Picture("swan.jpg");
        // Picture swan2 = new Picture("swan.jpg");

        // System.out.println("swan and swan2 are the same: " + isSame(swan, swan2));

        // swan = testClearLow(swan);
        // System.out.println(
        // "swan and swan2 are the same (after clearLow run on swan): " + isSame(swan, swan2));
        // // should return:
        // // Swan and swan2 are the same: true
        // // Swan and swan2 are the same (after clearLow run on swan): false
        // // // // // //

        // findDifferences Sample Code
        Picture arch = new Picture("arch.jpg");
        Picture arch2 = new Picture("arch.jpg");
        Picture koala = new Picture("koala.jpg");
        Picture robot1 = new Picture("robot.jpg");

        ArrayList<Point> pointList = findDifferences(arch, arch2);
        System.out.println("PointList after comparing two identical pictures has a size of "
                + pointList.size());

        pointList = findDifferences(arch, koala);
        System.out.println("PointList after comparing two different sized pictures has a size of "
                + pointList.size());

        Picture arch3 = hidePicture(arch, robot1, 65, 102);
        pointList = findDifferences(arch, arch3);
        System.out.println("PointList after hiding a picture has a size of " + pointList.size());

        arch.show();
        arch3.show();

        // showDifferentArea Sample Code


    }
}
