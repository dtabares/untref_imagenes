package sample;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MouseUtilities {
    public void mouseDragged(MouseEvent evt) {

/* Esta funcion puede servir para la funcionalidad de seleccionar una parte de la imagen...

if (dragging == false)
            return;  // Nothing to do because the user isn't drawing.

        double x = evt.getX();   // x-coordinate of mouse.
        double y = evt.getY();   // y-coordinate of mouse.

        if (x < 3)                          // Adjust the value of x,
            x = 3;                           //   to make sure it's in
        if (x > canvas.getWidth() - 57)       //   the drawing area.
            x = (int)canvas.getWidth() - 57;

        if (y < 3)                          // Adjust the value of y,
            y = 3;                           //   to make sure it's in
        if (y > canvas.getHeight() - 4)       //   the drawing area.
            y = canvas.getHeight() - 4;

        g.strokeLine(prevX, prevY, x, y);  // Draw the line.

        prevX = x;  // Get ready for the next line segment in the curve.
        prevY = y;*/

    } // end mouseDragged()

    public void getMousePosition(ImageView imageView)
    {
        // the following line allows detection of clicks on transparent
        // parts of the image:
        imageView.setPickOnBounds(true);
        imageView.setOnMouseClicked(event -> {
            System.out.println("["+event.getX()+", "+event.getY()+"]");
            //double[] mousePosition = new double [event.getX(),event.getY()];
            //return mousePosition;
        });


    }
}
