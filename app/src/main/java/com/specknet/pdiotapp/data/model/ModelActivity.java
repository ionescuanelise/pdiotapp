package com.specknet.pdiotapp.data.model;

public class ModelActivity {
    private String activity_label;
    private int activity_image;

    // Constructor
    public ModelActivity(String activity_label, int activity_image) {
        this.activity_label = activity_label;
        this.activity_image = activity_image;
    }

    // Getter and Setter
    public String getActivity_label() {
        return activity_label;
    }

    public void setActivity_label(String activity_label) {
        this.activity_label = activity_label;
    }

    public int getActivity_image() {
        return activity_image;
    }

    public void setActivity_image(int activity_image) {
        this.activity_image = activity_image;
    }
}
