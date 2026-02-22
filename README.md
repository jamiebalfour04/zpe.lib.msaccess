<h1>zpe.lib.msaccess</h1>

<p>
  This is the official Microsoft Access plugin for ZPE.
</p>

<p>
  The plugin provides support for reading .accdb files directly from ZPE.
</p>

<h2>Installation</h2>

<p>
  Place <strong>zpe.lib.msaccess.jar</strong> in your ZPE native-plugins folder and restart ZPE.
</p>

<p>
  You can also download with the ZULE Package Manager by using:
</p>
<p>
  <code>zpe --zule install zpe.lib.msaccess.jar</code>
</p>

<h2>Documentation</h2>

<p>
  Full documentation, examples and API reference are available here:
</p>

<p>
  <a href="https://www.jamiebalfour.scot/projects/zpe/documentation/plugins/zpe.lib.msaccess/" target="_blank">
    View the complete documentation
  </a>
</p>

<h2>Example</h2>

<pre>

import "zpe.lib.msaccess"

db = open_access_file("database.accdb")

tables = db.get_tables()
print(tables)

users = db.get_table("Users")
rows = users.get_rows()

for (r in rows)
    print(r.get_column("Name"))
end for
</pre>

<h2>Notes</h2>

<ul>
  <li>Supports .accdb files.</li>
  <li>Read-only access.</li>
  <li>Uses Jackcess internally.</li>
</ul>
