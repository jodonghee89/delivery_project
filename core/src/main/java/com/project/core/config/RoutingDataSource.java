package com.project.core.config;

import com.project.core.enums.DadaSourceType;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class RoutingDataSource extends AbstractRoutingDataSource {
  @Override
  protected Object determineCurrentLookupKey() {
    return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ?
        DadaSourceType.SECONDARY.name().toLowerCase() : DadaSourceType.PRIMARY.name().toLowerCase();
  }
}
