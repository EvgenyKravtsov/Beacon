package kgk.beacon.actions.event;

/**
 * Класс, представляющий результат http-запроса на включение/выключение
 * режима поиска для маячка Actis
 */
public class ToggleSearchModeEvent {

    /**
     * Результат запроса, true - запрос корректно принят сервером
     * */
    private boolean result;

    ////

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
