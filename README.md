# storage.io
Storage IO is an infrastructure library that provides unified access to various storage services, such as Dropbox,
Microsoft OneDrive, Google Drive, Box.net, local file system and potentially others, via a standard API.

The library supports pluggable approach (allows plugging of new implementations) and extensional support (allows extending core behavior).

## Example

```Java

DropboxCredential credentials = ...
DropboxStorageServiceProvider provider = ...

FolderMetadata folder = provider.listEntries(credentials, "/")

```


