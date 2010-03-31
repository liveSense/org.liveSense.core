/*
 *  Copyright 2010 Robert Csakany <robson@semmi.se>.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.liveSense.utils;

import java.util.HashMap;
import java.util.Iterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Robert Csakany (robson@semmi.se)
 * @created Feb 13, 2010
 */
public class AdministrativeService {


    Logger log = LoggerFactory.getLogger(AdministrativeService.class);

    private HashMap<String,Session> administrativeSessions = new HashMap<String, Session>();

    
    /**
     * Returns an administrative session to the default workspace.
     */
    public Session getAdministrativeSession(SlingRepository repository, boolean separatedSession) throws RepositoryException {
        synchronized(administrativeSessions) {
            if (separatedSession) {
                Session newSession = repository.loginAdministrative(null);
                administrativeSessions.put(newSession.toString(), newSession);
            } else {
                Iterator<String> iter = administrativeSessions.keySet().iterator();

                while (iter.hasNext()) {
                    String key = iter.next();
                    if (administrativeSessions.get(key).isLive()) {
                        return administrativeSessions.get(key);
                    } else {
                        administrativeSessions.remove(key);
                    }
                }

                Session newSession = repository.loginAdministrative(null);
                administrativeSessions.put(newSession.toString(), newSession);
                return newSession;
            }
        }
        return null;
    }


    /**
     * Returns an administrative session to the default workspace.
     */
    public Session getAdministrativeSession(SlingRepository repository) throws RepositoryException {
        return getAdministrativeSession(repository, false);
    }


    /**
     * Release the administrative service
     */
    public void releaseAdministrativeSession(Session session) throws RepositoryException {
        releaseAdministrativeSession(session, false);
    }


    /**
     * Release the administrative service
     */
    public void releaseAdministrativeSession(final Session session, boolean force) throws RepositoryException {
        synchronized (administrativeSessions) {
            if (session != null && session.isLive()) {
                if (session.hasPendingChanges()) {
                    session.save();
                    if (force) {
                        session.logout();
                        administrativeSessions.remove(session.toString());
                    }
                } else {
                    administrativeSessions.remove(session.toString());
                }
            }
        }
    }

    public void releaseAllAdministrativeSession() {
        synchronized(administrativeSessions) {
            Iterator<String> iter = administrativeSessions.keySet().iterator();

            while (iter.hasNext()) {
                String key = iter.next();
                if (administrativeSessions.get(key).isLive()) {
                    administrativeSessions.get(key).logout();
                    administrativeSessions.remove(key);
                } else {
                    administrativeSessions.remove(key);
                }
            }
        }
        
    }

    public void deactivate(ComponentContext componentContext) throws RepositoryException {
        releaseAllAdministrativeSession();
    }
}
