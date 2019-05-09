/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.drill.contrib.function;

import io.netty.buffer.DrillBuf;
import org.apache.drill.exec.expr.DrillSimpleFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.expr.annotations.Output;
import org.apache.drill.exec.expr.annotations.Param;
import org.apache.drill.exec.expr.annotations.Workspace;
import org.apache.drill.exec.expr.holders.VarCharHolder;

import java.util.HashMap;
import javax.inject.Inject;


public class MacAddressFunction {

  @FunctionTemplate(name = "getVendorName", scope = FunctionTemplate.FunctionScope.SIMPLE, nulls = FunctionTemplate.NullHandling.NULL_IF_NULL)

  public static class getVendorNameFunction implements DrillSimpleFunc {

    //private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getVendorNameFunction.class);

    @Param
    VarCharHolder inputText;

    @Output
    VarCharHolder out;

    @Inject
    DrillBuf buffer;

    @Workspace
    HashMap<String, String> vendors;

    public void setup() {
      java.io.InputStream vendorFile = getClass().getClassLoader().getResourceAsStream("nmap-mac-prefixes.txt");
      vendors = new HashMap<String, String>();
      try {
        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(vendorFile));

        String line;
        String key;
        String value;
        while ((line = br.readLine()) != null) {
          key = line.substring(0, 6);
          value = line.substring(7);
          vendors.put(key, value);
        }
      } catch (java.io.IOException e) {
        //logger.error("IOException encountered:  Could not read Vendor DB");
      }
    }

    public void eval() {
      String mac = org.apache.drill.exec.expr.fn.impl.StringFunctionHelpers.toStringFromUTF8(inputText.start, inputText.end, inputText.buffer);
      String vendorName;

      mac = mac.toUpperCase();
      mac = mac.replace("-", "");
      mac = mac.replace(":", "");
      mac = mac.replace(" ", "");
      mac = mac.substring(0, 6);

      try {
        vendorName = (String) vendors.get(mac);
      } catch (Exception e) {
        vendorName = "Unknown";
        //logger.info("OUI " + mac + " not in Database");
      }

      out.buffer = buffer;
      out.start = 0;
      out.end = vendorName.getBytes().length;
      buffer.setBytes(0, vendorName.getBytes());
    }
  }
}

