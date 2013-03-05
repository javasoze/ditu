package ditu.gateway;

import java.util.Comparator;
import java.util.Set;

import org.json.JSONObject;

import proj.zoie.impl.indexing.StreamDataProvider;

import com.senseidb.gateway.SenseiGateway;
import com.senseidb.indexing.DataSourceFilter;
import com.senseidb.indexing.ShardingStrategy;

public class DituOSMGateway extends SenseiGateway<JSONObject> {

  @Override
  public StreamDataProvider<JSONObject> buildDataProvider(
      DataSourceFilter<JSONObject> filter, String oldOffset, ShardingStrategy shardingStrategy,
      Set<Integer> partitions) throws Exception {
    return new DituOSMPbfReader(config, getVersionComparator());
  }

  @Override
  public Comparator<String> getVersionComparator() {
    return SenseiGateway.DEFAULT_VERSION_COMPARATOR;
  }

}
