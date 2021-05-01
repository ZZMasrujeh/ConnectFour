package models;

import javafx.scene.shape.Shape3D;


public class MovableModel extends MovableObject {

    private Shape3D node;

    public MovableModel(Shape3D node) {
        super(node);

        this.node = node;
    }

    public Shape3D getModel() {
        return node;
    }
}
