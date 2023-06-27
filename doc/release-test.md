# Release testing
## Prerequisites
- Testserver with release candidate available
- OIDC user with admin permissions on testserver
- Following libraries installed in R:
  - cli
  - getPass
  - arrow
  - httr
  - jsonlite
  - future
  - MolgenisArmadillo (2.0.0 >)
  - DSI
  - dsBaseClient
  - DSMolgenisArmadillo (2.0.0 >)
  - resourcer (1.4.0 >)
  
*For full testing*
- Admin password by hand

*Minimal*
- OIDC user with admin/superuser permissions on testserver  
- Someone with admin permissions available to take and regrant admin permissions to OIDC (super)user

With minimal prerequisites met, tests for basic auth will be skipped and permissions will have to be set by hand when
asked by script.

## Running the tests
1. Open your commandline
2. `cd` to the `scripts` folder of this repository
3. To run tests, type: `Rscript release-test.R`
4. Follow the directions in the script

If the script fails somewhere during the process, make sure you give your OIDC account back their admin permissions and 
throw away the projects: cohort1, cohort2 and omics.
