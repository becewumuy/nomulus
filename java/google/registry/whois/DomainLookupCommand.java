// Copyright 2016 The Nomulus Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package google.registry.whois;

import com.google.common.net.InternetDomainName;
import google.registry.model.domain.DomainResource;
import javax.annotation.Nullable;
import org.joda.time.DateTime;

/** Represents a WHOIS lookup on a domain name (i.e. SLD). */
class DomainLookupCommand extends DomainOrHostLookupCommand<DomainResource> {

  DomainLookupCommand(InternetDomainName domainName) {
    this(domainName, null);
  }

  public DomainLookupCommand(InternetDomainName domainName, @Nullable InternetDomainName tld) {
    super(domainName, tld, "Domain");
  }

  @Override
  WhoisResponse getSuccessResponse(DomainResource domain, DateTime now) {
    return new DomainWhoisResponse(domain, now);
  }
}
