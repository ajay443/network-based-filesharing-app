/*
 * Copyright (C) 2017.  FileSharingSystem - https://github.com/ajayramesh23/FileSharingSystem
 * Programming Assignment from Professor Z.Lan
 * @author Ajay Ramesh
 * @author Chandra Kumar Basavaraj
 * Last Modified - 3/15/17 6:44 PM
 */

package cs550.pa3.processor;

import cs550.pa3.helpers.PeerFile;
import cs550.pa3.helpers.Util;

public class Pull  implements Event {

    private PeerImpl peerImpl;

    public Pull(PeerImpl peerImpl) {
        this.peerImpl = peerImpl;
    }

    @Override
    public void trigger() {
        pull();
    }

    public void pull() {
        if (Util.getValue("pull.switch").equals("on")) {
            if (peerImpl == null || peerImpl.peerFiles == null) return;
            for (PeerFile f : peerImpl.peerFiles) {
                if (f.fileExpired()) {
                    peerImpl.pullFile(f);
                }
            }
        }
    }

}
