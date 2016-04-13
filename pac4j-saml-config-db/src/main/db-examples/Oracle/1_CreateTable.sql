--
--   Copyright 2012 - 2015 pac4j organization
--
--   Licensed under the Apache License, Version 2.0 (the "License");
--   you may not use this file except in compliance with the License.
--   You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
--   Unless required by applicable law or agreed to in writing, software
--   distributed under the License is distributed on an "AS IS" BASIS,
--   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--   See the License for the specific language governing permissions and
--   limitations under the License.
--

CREATE TABLE PAC4J_CFG 
(
  SSPCC_ID NUMBER NOT NULL,
  CLIENT_NAME VARCHAR2(200 CHAR) NOT NULL,
  ENVIRONMENT VARCHAR2(20 CHAR) NOT NULL,
  KEYSTORE_DATA BLOB NOT NULL,
  KEYSTORE_PASSWORD VARCHAR2(50 CHAR),
  PRIVATE_KEY_PASSWORD VARCHAR2(50 CHAR),
  IDP_METADATA CLOB NOT NULL,
  IDP_ENTITY_ID VARCHAR2(200 CHAR) NOT NULL,
  SP_ENTITY_ID VARCHAR2(200 CHAR) NOT NULL,
  MAX_AUTH_LIFETIME NUMBER DEFAULT 3600 NOT NULL,
  DEST_BINDING_TYPE VARCHAR2(200 CHAR),
  CONSTRAINT PAC4J_CFG_PK PRIMARY KEY (SSPCC_ID) ENABLE,
  CONSTRAINT PAC4J_CFG_UK1 UNIQUE (CLIENT_NAME,ENVIRONMENT) ENABLE,
  CONSTRAINT PAC4J_CFG_CHK1 CHECK (MAX_AUTH_LIFETIME > 0) ENABLE
);
COMMENT ON COLUMN PAC4J_CFG.SSPCC_ID IS 'Primary key';
COMMENT ON COLUMN PAC4J_CFG.CLIENT_NAME IS 'PAC4J client name; unique';
COMMENT ON COLUMN PAC4J_CFG.ENVIRONMENT IS 'Application environment to which this client config applies';
COMMENT ON COLUMN PAC4J_CFG.KEYSTORE_DATA IS 'JKS binary data; replacement of a JKS file on disk';
COMMENT ON COLUMN PAC4J_CFG.KEYSTORE_PASSWORD IS 'Password for the JKS keystore';
COMMENT ON COLUMN PAC4J_CFG.PRIVATE_KEY_PASSWORD IS 'Password for a single alias (private key) in the JKS keystore';
COMMENT ON COLUMN PAC4J_CFG.IDP_METADATA IS 'IdP metadata in XML format';
COMMENT ON COLUMN PAC4J_CFG.IDP_ENTITY_ID IS 'IdP entity ID';
COMMENT ON COLUMN PAC4J_CFG.SP_ENTITY_ID IS 'SP entity ID';
COMMENT ON COLUMN PAC4J_CFG.MAX_AUTH_LIFETIME IS 'Maximum authentication lifetime in seconds';
COMMENT ON COLUMN PAC4J_CFG.DEST_BINDING_TYPE IS 'Destination binding type as defined by SAML (POST or Redirect URIs)';
