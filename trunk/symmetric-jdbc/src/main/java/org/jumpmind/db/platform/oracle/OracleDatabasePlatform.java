package org.jumpmind.db.platform.oracle;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.platform.AbstractJdbcDatabasePlatform;
import org.jumpmind.db.platform.DatabaseNamesConstants;
import org.jumpmind.db.sql.SqlTemplateSettings;

/*
 * Provides support for the Oracle platform.
 */
public class OracleDatabasePlatform extends AbstractJdbcDatabasePlatform {

    /* The standard Oracle jdbc driver. */
    public static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";

    /* The old Oracle jdbc driver. */
    public static final String JDBC_DRIVER_OLD = "oracle.jdbc.dnlddriver.OracleDriver";

    /* The thin subprotocol used by the standard Oracle driver. */
    public static final String JDBC_SUBPROTOCOL_THIN = "oracle:thin";

    /* The thin subprotocol used by the standard Oracle driver. */
    public static final String JDBC_SUBPROTOCOL_OCI8 = "oracle:oci8";

    /* The old thin subprotocol used by the standard Oracle driver. */
    public static final String JDBC_SUBPROTOCOL_THIN_OLD = "oracle:dnldthin";

    /*
     * Creates a new platform instance.
     */
    public OracleDatabasePlatform(DataSource dataSource, SqlTemplateSettings settings) {
        super(dataSource, settings);
    }

    @Override
    protected OracleDdlBuilder createDdlBuilder() {
        return new OracleDdlBuilder();
    }

    @Override
    protected OracleDdlReader createDdlReader() {
        return new OracleDdlReader(this);
    }

    @Override
    protected OracleJdbcSqlTemplate createSqlTemplate() {
        return new OracleJdbcSqlTemplate(dataSource, settings, new OracleLobHandler(), getDatabaseInfo());
    }

    public String getName() {
        return DatabaseNamesConstants.ORACLE;
    }

    public String getDefaultCatalog() {
        return null;
    }

    public String getDefaultSchema() {
        if (StringUtils.isBlank(defaultSchema)) {
            defaultSchema = (String) getSqlTemplate().queryForObject(
                    "SELECT sys_context('USERENV', 'CURRENT_SCHEMA') FROM dual", String.class);
        }
        return defaultSchema;
    }

    @Override
    public boolean canColumnBeUsedInWhereClause(Column column) {
        String jdbcTypeName = column.getJdbcTypeName();
        return !column.isOfBinaryType() || "RAW".equals(jdbcTypeName);
    }

}