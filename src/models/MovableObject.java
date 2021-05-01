package models;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class MovableObject {
    public enum MovableObjectState {
        MOVING,
        NOT_MOVING
    }
    private Node node;

    private double startXPosition = 0;
    private double startYPosition = 0;
    private double startZPosition = 0;
    private double startXRotation = 0;
    private double startYRotation = 0;
    private double startZRotation = 0;

    private double xPosition = 0;
    private double yPosition = 0;
    private double zPosition = 0;
    private double xRotation = 0;
    private double yRotation = 0;
    private double zRotation = 0;

    private double originXPosition;
    private double originYPosition;
    private double originZPosition;
    private double originXRotation;
    private double originYRotation;
    private double originZRotation;

    private double destinationXPosition;
    private double destinationYPosition;
    private double destinationZPosition;
    private double destinationXRotation;
    private double destinationYRotation;
    private double destinationZRotation;

    private double scale = 1;

    private static Point3D xAxis = new Point3D(1, 0, 0);
    private static Point3D yAxis = new Point3D(0, 1, 0);
    private static Point3D zAxis = new Point3D(0, 0, 1);

    private MovableObjectState state = MovableObjectState.NOT_MOVING;

    MovableObject(Node node) {
        this.node = node;
    }

    private double maxTimer = 0;
    private double timer = 0;

    /***
     * Clears previous matrices and adds new matrices, to move and rotate the object.
     */
    private void applyTransforms() {
        node.getTransforms().clear();
        node.getTransforms().addAll(
                new Translate(xPosition, yPosition, zPosition),
                new Rotate(xRotation, xAxis),
                new Rotate(yRotation, yAxis),
                new Rotate(zRotation, zAxis)
        );
    }

    public void update(long elapsed) {
         // Function executes only when the object should move
        if (state == MovableObjectState.NOT_MOVING) return;

        timer += elapsed;
        double t = timer / maxTimer;
        if (t >= 1) {
            //intended animation time is finished
            xPosition = destinationXPosition;
            yPosition = destinationYPosition;
            zPosition = destinationZPosition;

            xRotation = destinationXRotation;
            timer = 0;
            state = MovableObjectState.NOT_MOVING;
        } else {
            //timer has not yet reached max time, setting the new position by interpolating the origin and
            //destination of the movement
            xPosition = (1 - t) * originXPosition + t * destinationXPosition;
            yPosition = (1 - t) * originYPosition + t * destinationYPosition;
            zPosition = (1 - t) * originZPosition + t * destinationZPosition;

            xRotation = (1 - t) * originXRotation + t * destinationXRotation;
            yRotation = (1 - t) * originYRotation + t * destinationYRotation;
            zRotation = (1 - t) * originZRotation + t * destinationZRotation;
        }

        applyTransforms();
    }

    public void scale(double scale) {
        this.scale = scale;
        this.node.setScaleX(scale);
        this.node.setScaleY(scale);
        this.node.setScaleZ(scale);
    }

    public void moveLeft(double distance){
        xPosition -= distance / scale;
        applyTransforms();
    }
    public void moveRight(double distance){
        xPosition += distance / scale;
        applyTransforms();
    }
    public void moveUp(double distance){
        yPosition -= distance / scale;
        applyTransforms();
    }
    public void moveDown(double distance){
        yPosition += distance / scale;
        applyTransforms();
    }
    public void moveFront(double distance){
        zPosition += distance / scale;
        applyTransforms();
    }
    public void moveBack(double distance){
        zPosition -= distance / scale;
        applyTransforms();
    }
    public void moveTo(double x, double y, double z) {
        xPosition += x / scale;
        yPosition += y / scale;
        zPosition += z / scale;
        applyTransforms();
    }

    public void setOrientationX(double angle) {
        destinationXRotation = angle;
        originXRotation = xRotation;
    }
    public void setOrientationY(double angle) {
        yRotation = angle;
        applyTransforms();
    }
    public void setOrientationZ(double angle) {
        zRotation = angle;
        applyTransforms();
    }
    public void rotateByX(double angle) {
        xRotation += angle;
        applyTransforms();
    }
    public void rotateByY(double angle) {
        yRotation += angle;
        applyTransforms();
    }
    public void rotateByZ(double angle) {
        zRotation += angle;
        applyTransforms();
    }

    private void prepareForAnimation(double maxTimer) {
        this.maxTimer = maxTimer;
        state = MovableObjectState.MOVING;

        originXPosition = xPosition;
        originYPosition = yPosition;
        originZPosition = zPosition;

        originXRotation = xRotation;
        originYRotation = yRotation;
        originZRotation = zRotation;
    }
    public void animateTo(double x, double y, double z, double maxTimer) {
        prepareForAnimation(maxTimer);

        destinationXPosition = x / scale;
        destinationYPosition = y / scale;
        destinationZPosition = z / scale;
    }
    public void animateRight(double distance, double maxTimer) {
        prepareForAnimation(maxTimer);

        destinationXPosition += distance / scale;
    }
    public void animateLeft(double distance, double maxTimer) {
        prepareForAnimation(maxTimer);

        destinationXPosition -= distance / scale;
    }
    public void animateDown(double distance, double maxTimer) {
        prepareForAnimation(maxTimer);

        destinationYPosition += distance / scale;
    }

    public double getY() {
        return yPosition;
    }
    public double getX() {
        return xPosition;
    }
    public double getZ() {
        return zPosition;
    }

    public boolean isMoving(){
        return state == MovableObjectState.MOVING;
    }

    /**
     * Sets the current position as the starting point for this model
     */
    public void setStartingPoint() {
        startXPosition = xPosition;
        startYPosition = yPosition;
        startZPosition = zPosition;

        startXRotation = xRotation;
        startYRotation = yRotation;
        startZRotation = zRotation;
    }

    /**
     * Stops animation if active and moves the object to the 0,0,0 or to the position that was set as starting point.
     */
    public void moveToStartingPoint() {
        if (state == MovableObjectState.MOVING) {
            state = MovableObjectState.NOT_MOVING;
        }
        xPosition = startXPosition;
        yPosition = startYPosition;
        zPosition = startZPosition;

        xRotation = startXRotation;
        yRotation = startYRotation;
        zRotation = startZRotation;
        applyTransforms();
    }
}
