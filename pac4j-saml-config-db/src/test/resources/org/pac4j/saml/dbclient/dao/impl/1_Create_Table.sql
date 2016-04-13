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

create table PAC4J_CFG
(
  SSPCC_ID IDENTITY NOT NULL,
  CLIENT_NAME VARCHAR(200) NOT NULL,
  ENVIRONMENT VARCHAR(20) NOT NULL,
  KEYSTORE_DATA BLOB NOT NULL,
  KEYSTORE_PASSWORD VARCHAR(50),
  PRIVATE_KEY_PASSWORD VARCHAR(50),
  IDP_METADATA CLOB NOT NULL,
  IDP_ENTITY_ID VARCHAR(200) NOT NULL,
  SP_ENTITY_ID VARCHAR(200) NOT NULL,
  MAX_AUTH_LIFETIME INT DEFAULT 3600 NOT NULL,
  DEST_BINDING_TYPE VARCHAR(200),
  CONSTRAINT PAC4J_CFG_PK PRIMARY KEY (SSPCC_ID),
  CONSTRAINT PAC4J_CFG_UK1 UNIQUE (CLIENT_NAME,ENVIRONMENT),
  CONSTRAINT PAC4J_CFG_CHK1 CHECK (MAX_AUTH_LIFETIME > 0)
);
