package models;

import java.util.ArrayList;

public class MovableModelGroup {
    private ArrayList<MovableModel> models = new ArrayList<>();

    public void addModel(MovableModel movableModel) {
        models.add(movableModel);
    }
    public void addModels(MovableModel... models) {
        for (MovableModel model : models) {
            addModel(model);
        }
    }

    public void update(long elapseMils) {
        for (MovableModel model : models) {
            model.update(elapseMils);
        }
    }

    public void scale(double scale) {
        for (MovableModel model : models) {
            model.scale(scale);
        }
    }

    void moveLeft(double distance) {
        for (MovableModel model : models) {
            model.moveLeft(distance);
        }
    }
    void moveDown(double distance) {
        for (MovableModel model : models) {
            model.moveDown(distance);
        }
    }
    void moveFront(double distance) {
        for (MovableModel model : models) {
            model.moveFront(distance);
        }
    }
    void moveUp(double distance) {
        for (MovableModel model : models) {
            model.moveUp(distance);
        }
    }
    public void moveTo(double x, double y, double z) {
        for (MovableModel model : models) {
            model.moveTo(x, y, z);
        }
    }

    void animateTo(double x, double y, double z, long maxTimer) {
        for (MovableModel model : models) {
            model.animateTo(x, y, z, maxTimer);
        }
    }

    public void setStartingPoint() {
        for (MovableModel model : models) {
            model.setStartingPoint();
        }
    }
    public void moveToStartingPoint() {
        for (MovableModel model : models) {
            model.moveToStartingPoint();
        }
    }
}
