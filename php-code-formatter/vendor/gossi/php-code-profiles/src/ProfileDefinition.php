<?php declare(strict_types=1);
namespace gossi\code\profiles;

use Symfony\Component\Config\Definition\Builder\NodeDefinition;
use Symfony\Component\Config\Definition\Builder\TreeBuilder;
use Symfony\Component\Config\Definition\ConfigurationInterface;

class ProfileDefinition implements ConfigurationInterface {
	public function getConfigTreeBuilder(): TreeBuilder {
		$treeBuilder = new TreeBuilder('formatter');
		$treeBuilder->getRootNode()
			->append($this->addIndentationSection())
			->append($this->addBracesSection())
			->append($this->addWhitespaceSection())
			->append($this->addNewlinesSection())
			->append($this->addBlanksSection())
		;

		return $treeBuilder;
	}

	private function addIndentationSection(): NodeDefinition {
		$treeBuilder = new TreeBuilder('indentation');
		$node = $treeBuilder->getRootNode();
		$node
			->children()
				->enumNode('character')
					->values(['tab', 'space'])
				->end()
				->integerNode('size')->end()
				->booleanNode('struct')->end()
				->booleanNode('function')->end()
				->booleanNode('blocks')->end()
				->booleanNode('switch')->end()
				->booleanNode('case')->end()
				->booleanNode('break')->end()
				->booleanNode('empty_lines')
			->end()
		;

		return $node;
	}

	private function addBracesSection(): NodeDefinition {
		$treeBuilder = new TreeBuilder('braces');
		$node = $treeBuilder->getRootNode();

		$node
			->children()
				->enumNode('struct')
					->values(['same', 'next'])
				->end()
				->enumNode('function')
					->values(['same', 'next'])
				->end()
				->enumNode('blocks')
					->values(['same', 'next'])
				->end()
			->end()
		;

		return $node;
	}

	private function addWhitespaceSection(): NodeDefinition {
		$treeBuilder = new TreeBuilder('whitespace');
		$node = $treeBuilder->getRootNode();

		$node
			->addDefaultsIfNotSet()
			->children()
				->arrayNode('default')
					->children()
						->booleanNode('before_curly')->end()
						->booleanNode('before_open')->end()
						->booleanNode('after_open')->end()
						->booleanNode('before_close')->end()
						->booleanNode('after_close')->end()
						->booleanNode('before_comma')->end()
						->booleanNode('after_comma')->end()
						->booleanNode('before_semicolon')->end()
						->booleanNode('after_semicolon')->end()
						->booleanNode('before_arrow')->end()
						->booleanNode('after_arrow')->end()
						->booleanNode('before_doublecolon')->end()
						->booleanNode('after_doublecolon')->end()
					->end()
				->end()
				->arrayNode('struct')
					->children()
						->booleanNode('before_curly')->defaultValue('default')->end()
						->booleanNode('before_comma')->defaultValue('default')->end()
						->booleanNode('after_comma')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('properties')
					->children()
						->booleanNode('before_comma')->defaultValue('default')->end()
						->booleanNode('after_comma')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('constants')
					->children()
						->booleanNode('before_comma')->defaultValue('default')->end()
						->booleanNode('after_comma')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('function')
					->children()
						->booleanNode('before_curly')->defaultValue('default')->end()
						->booleanNode('before_open')->defaultValue('default')->end()
						->booleanNode('after_open')->defaultValue('default')->end()
						->booleanNode('before_close')->defaultValue('default')->end()
						->booleanNode('before_comma')->defaultValue('default')->end()
						->booleanNode('after_comma')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('blocks')
					->children()
						->booleanNode('before_curly')->defaultValue('default')->end()
						->booleanNode('after_curly')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('ifelse')
					->children()
						->booleanNode('before_open')->defaultValue('default')->end()
						->booleanNode('after_open')->defaultValue('default')->end()
						->booleanNode('before_close')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('for')
					->children()
						->booleanNode('before_open')->defaultValue('default')->end()
						->booleanNode('after_open')->defaultValue('default')->end()
						->booleanNode('before_close')->defaultValue('default')->end()
						->booleanNode('before_comma')->defaultValue('default')->end()
						->booleanNode('after_comma')->defaultValue('default')->end()
						->booleanNode('before_semicolon')->defaultValue('default')->end()
						->booleanNode('after_semicolon')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('foreach')
					->children()
						->booleanNode('before_open')->defaultValue('default')->end()
						->booleanNode('after_open')->defaultValue('default')->end()
						->booleanNode('before_close')->defaultValue('default')->end()
						->booleanNode('before_arrow')->defaultValue('default')->end()
						->booleanNode('after_arrow')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('switch')
					->children()
						->booleanNode('before_open')->defaultValue('default')->end()
						->booleanNode('after_open')->defaultValue('default')->end()
						->booleanNode('before_close')->defaultValue('default')->end()
						->booleanNode('before_colon')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('while')
					->children()
						->booleanNode('before_open')->defaultValue('default')->end()
						->booleanNode('after_open')->defaultValue('default')->end()
						->booleanNode('before_close')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('catch')
					->children()
						->booleanNode('before_open')->defaultValue('default')->end()
						->booleanNode('after_open')->defaultValue('default')->end()
						->booleanNode('before_close')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('static')
					->children()
						->booleanNode('before_comma')->defaultValue('default')->end()
						->booleanNode('after_comma')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('global')
					->children()
						->booleanNode('before_comma')->defaultValue('default')->end()
						->booleanNode('after_comma')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('echo')
					->children()
						->booleanNode('before_comma')->defaultValue('default')->end()
						->booleanNode('after_comma')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('yield')
					->children()
						->booleanNode('before_arrow')->defaultValue('default')->end()
						->booleanNode('after_arrow')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('field_access')
					->children()
						->booleanNode('before_arrow')->defaultValue('default')->end()
						->booleanNode('after_arrow')->defaultValue('default')->end()
						->booleanNode('before_doublecolon')->defaultValue('default')->end()
						->booleanNode('after_doublecolon')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('function_invocation')
					->children()
						->booleanNode('before_open')->defaultValue('default')->end()
						->booleanNode('after_open')->defaultValue('default')->end()
						->booleanNode('before_close')->defaultValue('default')->end()
						->booleanNode('before_comma')->defaultValue('default')->end()
						->booleanNode('after_comma')->defaultValue('default')->end()
						->booleanNode('before_arrow')->defaultValue('default')->end()
						->booleanNode('after_arrow')->defaultValue('default')->end()
						->booleanNode('before_doublecolon')->defaultValue('default')->end()
						->booleanNode('after_doublecolon')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('assignments')
					->children()
						->booleanNode('before_assignment')->end()
						->booleanNode('after_assignment')->end()
					->end()
				->end()
				->arrayNode('operators')
					->children()
						->booleanNode('before_binary')->end()
						->booleanNode('after_binary')->end()
						->booleanNode('before_unary')->end()
						->booleanNode('after_unary')->end()
						->booleanNode('before_prefix')->end()
						->booleanNode('after_prefix')->end()
						->booleanNode('before_postfix')->end()
						->booleanNode('after_postfix')->end()
					->end()
				->end()
				->arrayNode('type_casts')
					->children()
						->booleanNode('before_open')->defaultValue('default')->end()
						->booleanNode('after_open')->defaultValue('default')->end()
						->booleanNode('before_close')->defaultValue('default')->end()
					->end()
				->end()
				->arrayNode('conditionals')
					->children()
						->booleanNode('before_questionmark')->end()
						->booleanNode('after_questionmark')->end()
						->booleanNode('before_colon')->end()
						->booleanNode('after_colon')->end()
					->end()
				->end()
				->arrayNode('grouping')
					->children()
						->booleanNode('before_open')->defaultValue('default')->end()
						->booleanNode('after_open')->defaultValue('default')->end()
						->booleanNode('before_close')->defaultValue('default')->end()
						->booleanNode('after_close')->defaultValue('default')->end()
					->end()
				->end()
			->end()
		;

		return $node;
	}

	private function addNewlinesSection(): NodeDefinition {
		$treeBuilder = new TreeBuilder('newlines');
		$node = $treeBuilder->getRootNode();

		$node
			->children()
				->booleanNode('elseif_else')->end()
				->booleanNode('catch')->end()
				->booleanNode('finally')->end()
				->booleanNode('do_while')->end()
			->end()
		;

		return $node;
	}

	private function addBlanksSection(): NodeDefinition {
		$treeBuilder = new TreeBuilder('blanks');
		$node = $treeBuilder->getRootNode();

		$node
			->children()
				->integerNode('before_namespace')->end()
				->integerNode('after_namespace')->end()
				->integerNode('after_use')->end()
				->integerNode('before_struct')->end()
				->integerNode('before_traits')->end()
				->integerNode('before_constants')->end()
				->integerNode('before_fields')->end()
				->integerNode('before_methods')->end()
				->integerNode('beginning_method')->end()
				->integerNode('end_method')->end()
				->integerNode('end_struct')->end()
				->integerNode('end_file')->end()
			->end()
		;

		return $node;
	}
}
