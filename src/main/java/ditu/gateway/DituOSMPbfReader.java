package ditu.gateway;

import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.pbf2.v0_6.PbfReader;

import proj.zoie.api.DataConsumer.DataEvent;
import proj.zoie.impl.indexing.StreamDataProvider;

public class DituOSMPbfReader extends StreamDataProvider<JSONObject> {

  private static Logger logger = Logger.getLogger(DituOSMPbfReader.class);
  static final String File_Property = "file";
  static final int DEFAULT_CAPACITY = 10000;
  private final PbfReader reader;
  private final LinkedBlockingDeque<JSONObject> queue;
  private int count = 0;
  private int processed = 0;
  private ReaderThread thread = new ReaderThread();
  
  
  public DituOSMPbfReader(Map<String, String> config, Comparator<String> versionComparator) {
    super(versionComparator);
    queue = new LinkedBlockingDeque<JSONObject>(DEFAULT_CAPACITY);
    String fileName = config.get(File_Property);
    File file = new File(fileName);
    reader = new PbfReader(file, 1);
    reader.setSink(new Sink(){

      @Override
      public void initialize(Map<String, Object> conf) {
        
      }

      @Override
      public void complete() {
        logger.info("completed");
      }

      @Override
      public void release() {
        
      }

      @Override
      public void process(EntityContainer entityContainer) {
        Entity e = entityContainer.getEntity();
        
        String place=null;
        
        String populationString = null;
        StringBuilder nameBuf = new StringBuilder();
        for (Tag tag : e.getTags()) {
          String key = tag.getKey();
          if (key == null) continue;
          if (key.equalsIgnoreCase("place")) {           
            place = tag.getValue();
          }
          else if (key.startsWith("name")){
            nameBuf.append(tag.getValue()).append(" ");
          }
          else if (key.startsWith("population")){
            populationString = tag.getValue();
          }
        }
        
        String name = nameBuf.toString();
        // We only care about places
        if (place != null && !place.isEmpty()){
          JSONObject jsonObj = new JSONObject();
          
        
          long id = e.getId();
          try {
            jsonObj.put("id", id);            
            jsonObj.put("place", place);
            jsonObj.put("contents", name);
            
            if (populationString != null){
              try{
                jsonObj.put("population", Long.parseLong(populationString));
              }
              catch(Exception ex){
                logger.error(ex);
              }
            }
            
            // Get Lat/Lon
            if (e instanceof Node) {
              Node n = (Node) e;
              jsonObj.put("lat", n.getLatitude());
              jsonObj.put("lon", n.getLongitude());
           }
            
            boolean success = false;
            while (!success){
              synchronized(queue){
                success = queue.offer(jsonObj);
              }
              if (success) break;
                try {
                  Thread.sleep(10000);
                  logger.info("cannot offer obj");
                } catch (InterruptedException e1) {
              }
            }
            processed++;
            
          } catch (JSONException e1) {
            logger.error(e1);
          }
        }
      }
      
    });
  }

  @Override
  public void start() {
    try{
      super.start();
    }
    finally{
      thread.start();
    }
    
  }
  
  @Override
  public void stop(){
    try{
      try {
        thread.join();
      } catch (InterruptedException e) {

      }
    }
    finally{
      super.stop();
    }
  }

  private int numEmpty = 0;

  @Override
  public DataEvent<JSONObject> next() {
    JSONObject obj = null;
    
    synchronized(queue){
      obj = queue.poll();
    }
    if (obj != null){
      numEmpty = 0;
      return new DataEvent<JSONObject>(obj, String.valueOf(count++));
    }
    else{
      long toSleep = numEmpty*500;
      if (toSleep > 5000) toSleep = 5000;
      try {
        Thread.sleep(toSleep);
      } catch (InterruptedException e) {
        logger.error(e);
      }
      numEmpty++;
      return null;
    }
  }

  @Override
  public void reset() {
    
  }

  @Override
  public void setStartingOffset(String offset) {
    
  }
  
  private class ReaderThread extends Thread{
    ReaderThread(){
      
    }
    
    public void run(){
      reader.run();
    }
  }

}
