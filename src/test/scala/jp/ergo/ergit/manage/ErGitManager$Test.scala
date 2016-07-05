package jp.ergo.ergit.manage

import java.io.{File => JFile}

import better.files._
import jp.ergo.ergit.manage.exception.ErGitManageException
import jp.ergo.ergit.repository.{Branch, Repository}
import org.scalatest._


class ErGitManager$Test extends FlatSpec with Matchers with BeforeAndAfter {
  private val root = File("./src/test/resources")
  private val ergitFile = root / ".ergit"

  private val repository1: Repository = Repository("path/to/repository1", "repository1", Seq[Branch](), Seq[Branch]())
  private val repository2: Repository = Repository("path/to/repository2", "repository2", Seq[Branch](), Seq[Branch]())
  private val repository3: Repository = Repository("path/to/repository3", "repository3", Seq[Branch](), Seq[Branch]())

  behavior of "ErGitManager$Test"

  before {
    if (ergitFile.exists) ergitFile.delete()
  }

  after {
    if (ergitFile.exists) ergitFile.delete()
  }

  "init" should "create .egit directory and repos file" in {
    ErGitManager.init(root)
    root / ".ergit" exists() should be(true)
    root / ".ergit" / "repos" exists() should be(true)
  }

  "init" should "throw ErGitManageException" in {
    ErGitManager.init(root)
    a[ErGitManageException] should be thrownBy ErGitManager.init(root)
  }

  "addRepository" should "add repository" in {
    ErGitManager.init(root)
    ErGitManager.addRepository(root, repository1)
    ErGitManager.addRepository(root, repository2)

    val lines = root / ".ergit" / "repos" lines
    val array = lines.toArray
    array(0) should be("""repository1="path/to/repository1"""")
    array(1) should be("""repository2="path/to/repository2"""")
  }


  "addRepository" should "throws ErGitManageException" in {
    ErGitManager.init(root)
    ErGitManager.addRepository(root, repository1)
    a[ErGitManageException] should be thrownBy ErGitManager.addRepository(root, repository1)

  }

  "removeRepository" should "remove the repository" in {
    ErGitManager.init(root)
    ErGitManager.addRepository(root, repository1)
    ErGitManager.addRepository(root, repository2)

    ErGitManager.removeRepository(root, repository1)
    val lines = root / ".ergit" / "repos" lines
    val array = lines.toArray
    array(0) should be("""repository2="path/to/repository2"""")
  }


  "removeRepository" should "do nothing if no repository to remove found" in {
    ErGitManager.init(root)
    ErGitManager.addRepository(root, repository1)
    ErGitManager.addRepository(root, repository2)
    val linesBefore = root / ".ergit" / "repos" lines
    val arrayBefore = linesBefore.toArray
    arrayBefore.length should be(2)
    arrayBefore(0) should be("""repository1="path/to/repository1"""")
    arrayBefore(1) should be("""repository2="path/to/repository2"""")

    // the repository3 to remove has not been added. then do nothing.
    ErGitManager.removeRepository(root, repository3)
    
    val linesAfter = root / ".ergit" / "repos" lines
    val arrayAfter = linesAfter.toArray
    arrayAfter.length should be(2)
    arrayAfter(0) should be("""repository1="path/to/repository1"""")
    arrayAfter(1) should be("""repository2="path/to/repository2"""")
  }

  "isUnderErGit" should "return true" in {
    ErGitManager.init(root)
    val child = root / "child"
    child.createDirectory()
    ErGitManager.isUnderErGit(child) should be(true)
    child.delete()
  }

  "isUnderErGit" should "return false" in {
    ErGitManager.isUnderErGit(root) should be(false)
  }

  "getErGitRoot" should "return ErGit root directory" in {
    ErGitManager.init(root)
    val child = root / "child"
    child.createDirectory()

    ErGitManager.getErGitRoot(child).name should be(".ergit")
    child.delete()
  }

  "getErGitRoot" should "throw ErGitManageException" in {
    a[ErGitManageException] should be thrownBy ErGitManager.getErGitRoot(root)
  }



}