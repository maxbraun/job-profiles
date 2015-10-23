package com.github.maxbraun.jobprofiles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;

import hudson.model.Item;
import hudson.scm.CredentialsSVNAuthenticationProviderImpl;
import hudson.scm.SubversionSCM;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import net.oneandone.sushi.fs.ExistsException;
import net.oneandone.sushi.fs.NodeInstantiationException;
import net.oneandone.sushi.fs.World;
import net.oneandone.sushi.fs.svn.SvnFilesystem;
import net.oneandone.sushi.fs.svn.SvnNode;
import net.oneandone.sushi.util.Strings;
public class SvnNodeBuilder {

    private static final World world = new World();
    private final String scm;


    public SvnNodeBuilder(String scm) {
        this.scm = scm;
    }

    public SvnNode build() {
        String remote;
        remote = Strings.removeRightOpt(scm, "/");
        if (remote.startsWith("scm:")) {
            remote = Strings.removeLeft(remote, "scm:");
        }
        if (!remote.startsWith("svn:")) {
            remote = "svn:" + remote;
        }
        try {
            URI remoteUri = new URI(remote);


            List<DomainRequirement> domainRequirements = URIRequirementBuilder.fromUri(remote).build();
            SvnNode node;
            Item item = null;
            if ( Jenkins.getInstance() != null && Jenkins.getActiveInstance().getExtensionList(CredentialsProvider.class) != null) {
                StandardUsernamePasswordCredentials credentials = CredentialsMatchers.firstOrNull(
                  CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class, item, ACL.SYSTEM, domainRequirements),
                  CredentialsMatchers.always());



                SvnFilesystem svnFilesystem = (SvnFilesystem) world.getFilesystem("svn");

                if (credentials != null) {
                    ISVNAuthenticationManager svnAuthenticationManager =
                      SubversionSCM.createSvnAuthenticationManager(new CredentialsSVNAuthenticationProviderImpl(credentials));
                    svnFilesystem.setDefaultAuthenticationManager(svnAuthenticationManager);
                }
                node = svnFilesystem.node(remoteUri, null);
            } else {
              node = (SvnNode)world.node(remoteUri);
            }

            if (node.isFile()) {
                return node.getParent();
            } else {
                return node;
            }
        } catch (URISyntaxException e) {
            throw new JobProfileException(e.getMessage(), e);
        } catch (NodeInstantiationException e) {
            throw new JobProfileException(e.getMessage(), e);
        } catch (ExistsException e) {
            throw new JobProfileException(e.getMessage(), e);
        }
    }
}
