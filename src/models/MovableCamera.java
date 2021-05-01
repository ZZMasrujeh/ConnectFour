package models;

import javafx.scene.Camera;

public class MovableCamera extends MovableObject {
    private Camera camera;

    public MovableCamera(Camera camera) {
        super(camera);

        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }
}
