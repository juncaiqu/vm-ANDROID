package com.kdx.core.fragment;

import android.support.v4.app.Fragment;

/**
 * Author  :qujuncai
 * DATE    :18/12/6
 * Email   :qjchzq@163.com
 */
public abstract class AbsFragment extends Fragment {
    private String target = null;

    public AbsFragment(String target) {
        super();
        this.target = target;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "AbsFragment{" +
                "target='" + target + '\'' +
                "} " + super.toString();
    }
}
