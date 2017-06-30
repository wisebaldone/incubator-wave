
![Apache Wave](https://cdn.rawgit.com/wisebaldone/incubator-wave/b26efbae/assets/ApacheWaveCircleLogo.svg)
# Apache Wave

| Tool | Status |
| --- | --- |
| Jenkins | [![Build Status](https://builds.apache.org/buildStatus/icon?job=wave-small_tests)](https://builds.apache.org/job/wave-small_tests) |
| Travis | [![Build Status](https://travis-ci.org/apache/incubator-wave.svg?branch=master)](https://travis-ci.org/apache/incubator-wave) |



The Apache Wave project is a stand alone OT (Operational Transform) server and 
rich web client that serves as a Wave reference implementation.

| Place | Link |
| --- | --- |
| Website | https://incubator.apache.org/wave/ |
| Mailing List | https://incubator.apache.org/wave/mailing-lists.html |
| Slack | https://the-asf.slack.com/messages/wave |


This project lets developers and enterprise users run wave servers on their own 
hardware and offers a rich API to allow users to build upon the platform.

### Operational Transform

Operational Transforms are the backbone behind the concurrency control of 
the real time document editing. OT is the core technology used and exposed by
Apache Wave. The OT used by the project requires both client and server to 
agree on the method of transformation and as such the project aims to expose
generic libraries to allow developers to integrate the technology.

### Server ( Api is in progress )

Apache Wave offers a generic server backend with a set of well defined 
documents and data structures that developers can build upon to make a range
of applications and tools. Please see the documentation for developer examples 
or installation instructions.

### Web Client ( In Progress )

The main web client of the project is a interactive team communication platform
where multiple users can communicate on topics in real time. This client is 
based off the original Google Wave project but is only one of many uses of the 
platform.

## Getting Started & Documentation

All documentation can be found on the [Apache Wave website](https://incubator.apache.org/wave/).

* Maintainers please find your hub [here](MAINTAINERS.md)
* Server documentation can be found [here]()
* Client documentation can be found [here]()
* Wave Documents documentation can be found [here]()

## License

The Apache Wave project is licensed under the Apache 2 license. 
See [LICENSE](LICENSE) for more details.

## Apache Incubator

![Apache Wave Incubator](http://incubator.apache.org/images/incubator_feather_egg_logo.png)

Apache Wave has been an incubating project inside the Apache Incubator since 
2010 and through the resources and guidance provided by the Software Foundation
has been steadily growing since acceptance.

Please see [here](http://incubator.apache.org/projects/wave.html) for our 
incubating status.

## Cryptographic Software Notice

This distribution includes cryptographic software.  The country in
which you currently reside may have restrictions on the import,
possession, use, and/or re-export to another country, of
encryption software.  BEFORE using any encryption software, please
check your country's laws, regulations and policies concerning the
import, possession, or use, and re-export of encryption software, to
see if this is permitted.  See <http://www.wassenaar.org/> for more
information.

The U.S. Government Department of Commerce, Bureau of Industry and
Security (BIS), has classified this software as Export Commodity
Control Number (ECCN) 5D002.C.1, which includes information security
software using or performing cryptographic functions with asymmetric
algorithms.  The form and manner of this Apache Software Foundation
distribution makes it eligible for export under the License Exception
ENC Technology Software Unrestricted (TSU) exception (see the BIS
Export Administration Regulations, Section 740.13) for both object
code and source code.

The following provides more details on the included cryptographic
software:

  Apache Wave requires the BouncyCastle Java cryptography APIs:
    http://www.bouncycastle.org/java.html
