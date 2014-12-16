/**
 * Created by mac on 16.12.14.
 */
public class Model {
    public void init (){
        UI ui = new UI();
        ui.initUI().setVisible(true);
        ui.addOnClickListeners();
        ui.addActionListenerToChooseFile();
    }
}
