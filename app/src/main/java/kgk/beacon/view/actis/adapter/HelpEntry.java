package kgk.beacon.view.actis.adapter;

/**
 * ADT для одной подсказки в пользовательской инструкции
 */
public class HelpEntry {

    private String title;
    private CharSequence body;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CharSequence getBody() {
        return body;
    }

    public void setBody(CharSequence body) {
        this.body = body;
    }
}
