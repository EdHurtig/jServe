package jServe.Sites;

import jServe.Core.Request;

public class EchoSite extends Site {

    @Override
    public void onStart() {
        // Do Nothing
    }

    @Override
    public void run(Request r) {
        r.out.println(r.getRawRequest());
    }

}
