# bb-create-project-skeleton

a babashka utility to create the folders structures and the empty files from a file with content like:

```
your-project/
├── deps.edn
├── src/
│   └── my_web_server/
│       ├── core.clj
│       ├── config.clj
│       ├── server.clj
│       ├── db.clj
│       ├── handler.clj
│       └── repo.clj
├── resources/
│   └── migrations/
│       ├── 20230906000001-create-stories-table.up.sql
│       ├── 20230906000001-create-stories-table.down.sql
│       ├── 20230906000002-create-answers-table.up.sql
│       └── 20230906000002-create-answers-table.down.sql
└── dev/
    └── user.clj
```

## Usage

```
bb create_project_skeleton.clj schema.txt
```


