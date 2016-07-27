package kgk.beacon.monitoring.presentation.presenter;

import kgk.beacon.monitoring.domain.interactor.GetUser;
import kgk.beacon.monitoring.domain.interactor.InteractorThreadPool;
import kgk.beacon.monitoring.domain.model.User;
import kgk.beacon.monitoring.presentation.view.ProfileView;
import kgk.beacon.util.AppController;

public class ProfileViewPresenter implements GetUser.Listener {

    private ProfileView view;

    ////

    public ProfileViewPresenter(ProfileView view) {
        this.view = view;
    }

    ////

    public void unbindView() {
        view = null;
    }

    public void requestUser() {
        GetUser interactor = new GetUser();
        interactor.setListener(this);
        InteractorThreadPool.getInstance().execute(interactor);
    }

    ////

    @Override
    public void onUserRetreived(final User user) {
        AppController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.showUser(user);
            }
        });
    }
}
