CORS Header Scrutiny Filter
===========================

This project provides a CORS ([Cross-Origin Resource Sharing](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing)) HTTP servlet filter to guard against XSRF ([Cross-Site Request Forgery](https://www.owasp.org/index.php/Cross-Site_Request_Forgery_%28CSRF%29)).
The approach taken here is based on [CORS Origin Header Scrutiny (OWASP)](https://www.owasp.org/index.php/CORS_OriginHeaderScrutiny).

How to Use
==========

In the web.xml of your Java web application:

````
	<filter>
		<filter-name>CORSFilter</filter-name>
		<filter-class>com.tasktop.servlet.cors.CorsHeaderScrutinyServletFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>CORSFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
````

This filter should be added at the top of the web.xml so that it is invoked before other filters.

Building
========

From the command-line:

`mvn clean package`

Build status on [Travis CI](https://travis-ci.org/Tasktop/cors-servlet-filter): ![CI status](https://travis-ci.org/Tasktop/cors-servlet-filter.svg?branch=master "CI Status") 

How To Release
--------------

From the command-line:

````
mvn -Possrh -Psign -DpushChanges=false -DlocalCheckout=true '-Darguments=-Dgpg.passphrase=thesecret -Dgpg.keyname=keyname' release:clean release:prepare
mvn -Possrh -Psign -DpushChanges=false -DlocalCheckout=true '-Darguments=-Dgpg.passphrase=thesecret -Dgpg.keyname=keyname' release:perform
````

Then push changes:

````
git push
````

Search for the staging repository, close and relase it: https://oss.sonatype.org/#stagingRepositories

License
=======

Copyright (c) 2017 Tasktop Technologies

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
