/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.util;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * Unit test for PropertySplitter.
 * @since 1.0
 */
public class PropertySplitterTest
{
    @Test
    public void testPropertySplitter()
    {
        PropertySplitter splitter = new PropertySplitter();

        Map<String, String> result = splitter.map("key:value,key2:value2");
        assertTrue(result != null);
        assertTrue(result.get("key").equals("value"));
        assertTrue(result.get("key2").equals("value2"));
    }
}
