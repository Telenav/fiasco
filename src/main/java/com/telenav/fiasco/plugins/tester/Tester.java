//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.plugins.tester;

import com.telenav.fiasco.Module;
import com.telenav.fiasco.plugins.Plugin;

/**
 * Runs unit tests
 *
 * @author shibo
 */
public class Tester extends Plugin
{
    public Tester(final Module module)
    {
        super(module);
    }

    @Override
    protected void onRun()
    {
    }
}
