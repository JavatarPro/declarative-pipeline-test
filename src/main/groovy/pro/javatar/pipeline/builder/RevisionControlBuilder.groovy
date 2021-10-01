/**
 * Copyright Javatar LLC 2018 ©
 * Licensed under the License located in the root of this repository (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://github.com/JavatarPro/declarative-pipeline/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pro.javatar.pipeline.builder

import pro.javatar.pipeline.model.RevisionControlType
import pro.javatar.pipeline.model.Vcs
import pro.javatar.pipeline.model.VcsRepositoryType
import pro.javatar.pipeline.service.vcs.HgService
import pro.javatar.pipeline.service.vcs.GitService
import pro.javatar.pipeline.service.vcs.RevisionControlService
import pro.javatar.pipeline.service.vcs.VcsRepositoryUrlResolver
import pro.javatar.pipeline.util.Logger

/**
 * @author Borys Zora
 * @since 2018-03-09
 */
class RevisionControlBuilder implements Serializable {

    RevisionControlService build(Vcs vcs) {
        Logger.info("RevisionControlService.build() started")
        RevisionControlService result
        if (vcs.getUrl().endsWith(".git")) {
            new GitService(repo, credentialsId, repoOwner, flowPrefix)
        }
        if (type == RevisionControlType.MERCURIAL) {
            result = new HgService(repo, credentialsId, repoOwner, flowPrefix)
        } else if (type == RevisionControlType.GIT) {
            result = new GitService(repo, credentialsId, repoOwner, flowPrefix)
        }
        result.setUrlResolver(new VcsRepositoryUrlResolver(vcsRepositoryType, true, result))
        result.setDomain(domain)
        Logger.info("RevisionControlService.build() finished")
        return result
    }
}
