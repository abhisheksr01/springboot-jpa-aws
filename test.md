
# Onboarding

## Azure Access
----------------
    **A. Azure Cloud Platform - https://portal.azure.com/**

    This is where all our Azure Cloud Infrastructure will be provisioned.

    For this engagement, we have requested everyone (core team members) to have "contributor" access to an Azure Subscription "Hachette Livre UK DevOps PoC"    specifically created for us.

    Login to https://portal.azure.com/ using your Hachette credentials to access and create some resources to verify that you have the right privileges.

    > Note: You may be required to use your ADM credential for accessing the portal as a contributor with elevated privileges. 

    **B. Azure DevOps Platform - https://dev.azure.com/hukweb365/**

    This is where we will have our codebase stored in Azure Repos (VCS), Azure Boards and Pipelines (CICD).

    https://dev.azure.com/hukweb365/

## Tools Access

    You must download the below tools before you start working:
    
    - VSCode - https://code.visualstudio.com/
    - Git CLI - https://git-scm.com/book/en/v2/Getting-Started-Installing-Git
    - Azure CLI - https://learn.microsoft.com/en-us/cli/azure/install-azure-cli
    - Terraform Version 1.3.1 - https://www.terraform.io/downloads
      For Windows you can use below direct links:
      https://releases.hashicorp.com/terraform/1.3.1/terraform_1.3.1_windows_amd64.zip
      or
      https://releases.hashicorp.com/terraform/1.3.1/terraform_1.3.1_windows_386.zip
    
    Installing VS Code in Windows Hachette Laptops:
    Visit https://code.visualstudio.com/download and download the System Installer (64-bit)
    Run the installer using your CLI Admin account
    This will install the application in "C:\Program Files\Microsoft VS Code\Code.exe"
    Then make a batch file in notepad as follows and save to your desktop
    
    runas /user:hachette\adm-<username> /profile "C:\Program Files\Microsoft VS Code\Code.exe"
    pause
    
    Use the batch file to open VS Code under your ADM Admin profile to allow successful with Azure