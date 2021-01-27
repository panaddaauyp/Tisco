#!groovy​
@Library('utils')
import org.tisco.utilities.infrastructure.*

static String replaceSlash(text) {
	if(text){
		return text.replaceAll('/','\\\\/');
	} else {
		return text;
	}
} 
   
// echo ${env.JOB_NAME}
def mode = env.environment_mode
env.log_level = replaceSlash(env.log_level)
env.db_url = replaceSlash(env.db_url)
env.db_port = replaceSlash(env.db_port)
env.db_name = replaceSlash(env.db_name)
env.db_secretname = replaceSlash(env.db_secretname)
env.db_region = replaceSlash(env.db_region)
env.db_name_state2 = env.db_name_state2 ? replaceSlash(env.db_name_state2) : ""
env.db_secretname_state2 = env.db_secretname_state2 ? replaceSlash(env.db_secretname_state2) : ""
env.db_region_state2 = env.db_region_state2 ? replaceSlash(env.db_region_state2) : ""
env.db_name_state3 = env.db_name_state3 ? replaceSlash(env.db_name_state3) : ""
env.db_secretname_state3 = env.db_secretname_state3 ? replaceSlash(env.db_secretname_state3) : ""
env.db_region_state3 = env.db_region_state3 ? replaceSlash(env.db_region_state3) : ""

env.db_name_state4 = env.db_name_state4 ? replaceSlash(env.db_name_state4) : ""
env.db_secretname_state4 = env.db_secretname_state4 ? replaceSlash(env.db_secretname_state4) : ""
env.db_region_state4 = env.db_region_state4 ? replaceSlash(env.db_region_state4) : ""

def utils = new aws(mode)

def git_repo_url = scm.getUserRemoteConfigs()[0].getUrl()
def git_branch = scm.getBranches()[0].getName().tokenize('/')[1]

println "git_repo_url => " + git_repo_url
println "git_branch => " + git_branch
println "mode => " + mode


env.app_code = "digitalshelfbackend"
env.mvn_profile = "app"										
env.project_name = "DigitalShelfBackend"
env.logger_config = "APP"
env.logger_level = "DEBUG"


println "codedeploy_deployment_group => " + env.codedeploy_deployment_group		//"DEP-API-SERVICE
println "Retrieve tag_version => " + env.tag_version
println "Retrieve commit_hash => " + env.commit_hash

env.deploy_folder = "deployment"
env.build_folder = env.mvn_profile
env.deploy_package = env.project_name + "_build_" + env.BUILD_NUMBER + '.zip'
env.deploy_package_path = env.project_name + '/' + env.project_name + "_build_" + env.BUILD_NUMBER + '.zip'
env.deploy_package_old =  env.project_name + "_build_*.zip"
env.project_path = env.project_name + '/DigitalLending'

node {
        utils.set_script(this)
	try {
		
		if (env.commit_hash != "") {
            stage('Prepare - Git clone from Commit Hash'){
             checkout(
                 [$class: 'GitSCM', 
				 branches: [[name: env.commit_hash]],
                 userRemoteConfigs: [[url: git_repo_url]]]
             )
            }
        } else if (env.tag_version != "") {
			stage('Prepare - Git clone from Tag'){
			   checkout(
				   [$class: 'GitSCM', 
				   branches: [[name: 'refs/tags/' + env.tag_version]], 
				   userRemoteConfigs: [[url: git_repo_url]]]
			   )
			}
		} else {
			stage('Prepare - Git clone from Branch'){
				git branch: git_branch, url: git_repo_url
			}
		}
		
	    stage('Build'){
	        env.mvnHome = tool 'M3'
			dir('.') {
				sh '''
					cp $project_path/src/main/resources/template/db_template.properties $project_path/src/main/resources/db.properties
					cp $project_path/src/main/resources/template/db_template.json $project_path/src/main/resources/db.json
					rm -fr $build_folder $deploy_folder
					mkdir -p $build_folder $deploy_folder $build_folder/war
					sed -i "s/#db_url#/$db_url/g" $project_path/src/main/resources/db.properties
					sed -i "s/#db_port#/$db_port/g" $project_path/src/main/resources/db.properties
					sed -i "s/#db_secretname#/$db_secretname/g" $project_path/src/main/resources/db.properties
					sed -i "s/#db_region#/$db_region/g" $project_path/src/main/resources/db.properties

					sed -i "s/#db_name#/$db_name/g" $project_path/src/main/resources/db.json
					sed -i "s/#db_secretname#/$db_secretname/g" $project_path/src/main/resources/db.json
					sed -i "s/#db_region#/$db_region/g" $project_path/src/main/resources/db.json
					sed -i "s/#db_name_state2#/$db_name_state2/g" $project_path/src/main/resources/db.json
					sed -i "s/#db_secretname_state2#/$db_secretname_state2/g" $project_path/src/main/resources/db.json
					sed -i "s/#db_region_state2#/$db_region_state2/g" $project_path/src/main/resources/db.json
					sed -i "s/#db_name_state3#/$db_name_state3/g" $project_path/src/main/resources/db.json
					sed -i "s/#db_secretname_state3#/$db_secretname_state3/g" $project_path/src/main/resources/db.json
					sed -i "s/#db_region_state3#/$db_region_state3/g" $project_path/src/main/resources/db.json


					sed -i "s/#db_name_state4#/$db_name_state4/g" $project_path/src/main/resources/db.json
					sed -i "s/#db_secretname_state4#/$db_secretname_state4/g" $project_path/src/main/resources/db.json
					sed -i "s/#db_region_state4#/$db_region_state4/g" $project_path/src/main/resources/db.json

					$mvnHome/bin/mvn -f $project_path/pom.xml -P $mvn_profile clean install -Dmaven.test.skip=true
					cp $project_name/appspec.yml $build_folder/appspec.yml
					mv $project_path/target/*.war $build_folder/war/
					mv $project_name/scripts $build_folder

					cd $build_folder
					zip -r --exclude=*.git* --exclude=*config* --exclude=*.groovy* ../$deploy_folder/$deploy_package .
				'''
			}
	    }

		stage('New Instance'){
	        utils.newRevisionInstant_noPuppet()
	    }
		
		stage('Deploy'){
			dir('.') {
				utils.bluegreenDeploy(env.deploy_folder +"/"+env.deploy_package)
			}
	    }
	    
	} catch (e) {
		echo "Something went wrong terminate revision"
		if (mode=="alpha") {
			input "wait for terminate"
		}
		utils.terminateCurrentRevision()
		throw e
	}
	utils.terminateStage()
}