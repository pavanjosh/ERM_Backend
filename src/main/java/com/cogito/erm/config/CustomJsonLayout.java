package com.cogito.erm.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import com.cogito.erm.util.ERMUtil;

import java.util.Map;

public class CustomJsonLayout
    extends JsonLayout {

  @Override
  protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {
    map.put("msg", event.getMDCPropertyMap());
    map.put(ERMUtil.APPLICATION, ERMUtil.SYSTEM_NAME);
  }

}
