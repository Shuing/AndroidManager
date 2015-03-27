package ning.xyw.androidmanager.bean;

/**
 * Created by ning on 15-3-27.
 */
public class AppBean {
    private String label;
    private String packagename;

    public AppBean() {
    }

    public AppBean(String label, String packagename) {
        this.label = label;
        this.packagename = packagename;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }
}
